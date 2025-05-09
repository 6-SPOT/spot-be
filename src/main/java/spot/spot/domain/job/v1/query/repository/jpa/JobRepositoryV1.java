package spot.spot.domain.job.v1.query.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Job;

@Repository
@Deprecated
public interface JobRepositoryV1 extends JpaRepository<Job, Long> {
    @Query("""
    SELECT j FROM Job j
    WHERE j.startedAt IS NULL
      AND (6371 * acos(
               cos(radians(:lat)) * cos(radians(j.lat)) *
               cos(radians(j.lng) - radians(:lng)) +
               sin(radians(:lat)) * sin(radians(j.lat))
           )) < :dist
    ORDER BY (6371 * acos(
                cos(radians(:lat)) * cos(radians(j.lat)) *
                cos(radians(j.lng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(j.lat))
            )) ASC
    """)
    Slice<Job> findNearByJobWithJPQL(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("dist") double dist,
        Pageable pageable
    );

    @Query(value = """
    SELECT j.*
    FROM job j
    WHERE j.started_at IS NULL
      AND ST_Distance_Sphere(
               point(j.lng, j.lat),
               point(:lng, :lat)
           ) / 1000 < :dist -- 미터 단위이므로 km로 변환
    ORDER BY ST_Distance_Sphere(
                 point(j.lng, j.lat),
                 point(:lng, :lat)
             ) ASC
    LIMIT :pageSize OFFSET :offset
    """, nativeQuery = true)
    List<Job> findNearByJobWithNativeQuery(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("dist") double dist,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );
}
