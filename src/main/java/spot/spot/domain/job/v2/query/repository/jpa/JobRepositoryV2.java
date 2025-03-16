package spot.spot.domain.job.v2.query.repository.jpa;

import jakarta.persistence.QueryHint;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;

@Repository
public interface JobRepositoryV2 extends JpaRepository<Job, Long> {

    @QueryHints(@QueryHint(name = "javax.persistence.query.hint", value = "FORCE INDEX (idx_lat_lng)"))
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
      AND j.lat BETWEEN :lat - (:dist / 111.045)
                   AND :lat + (:dist / 111.045)
      AND j.lng BETWEEN :lng - (:dist / (111.045 * cos(radians(:lat))))
                   AND :lng + (:dist / (111.045 * cos(radians(:lat))))
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
    SELECT * FROM (
        SELECT
            j.id AS id,
            j.title AS title,
            j.content AS content,
            j.img AS picture,
            j.lat AS lat,
            j.lng AS lng,
            j.money AS money,
            (6371 * acos(
                cos(radians(:lat)) * cos(radians(j.lat)) *
                cos(radians(j.lng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(j.lat))
            )) AS dist,
            j.tid AS tid
        FROM job j FORCE INDEX (idx_lat_lng)  -- 인덱스 강제 적용
        WHERE j.started_at IS NULL
          AND j.lat BETWEEN :lat - (:dist / 111.045)
                       AND :lat + (:dist / 111.045)
          AND j.lng BETWEEN :lng - (:dist / (111.045 * cos(radians(:lat))))
                       AND :lng + (:dist / (111.045 * cos(radians(:lat))))
    ) AS subquery
    WHERE dist <= :dist  -- 바깥 WHERE에서 필터링
    ORDER BY dist ASC
    LIMIT :pageSize OFFSET :offset
    """, nativeQuery = true)
    List<NearByJobResponse> findNearByJobWithNativeQuery(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("dist") double dist,
        @Param("pageSize") int pageSize,
        @Param("offset") int offset
    );
}
