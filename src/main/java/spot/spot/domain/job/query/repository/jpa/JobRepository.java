package spot.spot.domain.job.query.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
    SELECT new spot.spot.domain.job.query.dto.response.NearByJobResponse(
        j.id, 
        j.title, 
        j.content, 
        j.img, 
        j.lat, 
        j.lng, 
        j.money, 
        (6371 * acos(
               cos(radians(:lat)) * cos(radians(j.lat)) *
               cos(radians(j.lng) - radians(:lng)) +
               sin(radians(:lat)) * sin(radians(j.lat))
           )),
        j.tid
    ) 
    FROM Job j
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
    Slice<NearByJobResponse> findNearByJobWithJPQL(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("dist") double dist,
        Pageable pageable
    );

    @Query(value = """
    SELECT 
        j.id AS id,
        j.title AS title,
        j.content AS content,
        j.img AS picture,
        j.lat AS lat,
        j.lng AS lng,
        j.money AS money,
        ST_Distance_Sphere(
            j.location, 
            ST_GeomFromText(:point, 4326)
        ) / 1000 AS dist, -- 미터를 km 단위로 변환
        j.tid AS tid
    FROM job j
    WHERE j.started_at IS NULL
      AND MBRContains(
          ST_Buffer(ST_GeomFromText(:point, 4326), :dist * 1000), j.location
      )
    ORDER BY ST_Distance_Sphere(
                 j.location, 
                 ST_GeomFromText(:point, 4326)
             ) ASC
    LIMIT :pageSize OFFSET :offset
    """, nativeQuery = true)
    List<NearByJobResponse> findNearByJobWithNativeQuery(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("dist") double dist,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );

    Optional<Job> findByTid(String tid);
}
