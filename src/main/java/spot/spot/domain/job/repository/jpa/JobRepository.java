package spot.spot.domain.job.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
    SELECT j FROM Job j
    WHERE j.startedAt IS NULL
      AND j.lat BETWEEN :lat - (:dist / 111.045) AND :lat + (:dist / 111.045)
      AND j.lng BETWEEN :lng - (:dist / (111.045 * cos(radians(:lat))))
                   AND :lng + (:dist / (111.045 * cos(radians(:lat))))
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
      AND j.lat BETWEEN :lat - (:dist / 111.045) 
                   AND :lat + (:dist / 111.045)
      AND j.lng BETWEEN :lng - (:dist / (111.045 * cos(radians(:lat)))) 
                   AND :lng + (:dist / (111.045 * cos(radians(:lat))))
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


    Optional<Job> findByTitle(String title);
}
