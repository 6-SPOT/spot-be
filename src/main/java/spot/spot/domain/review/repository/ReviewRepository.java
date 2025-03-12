package spot.spot.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.spot.domain.review.dto.response.CompletedJobReview;
import spot.spot.domain.review.entity.Review;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByJob_Id(Long jobId);// 특정 일(Job)에 대한 모든 리뷰 조회

    @Query("SELECT new spot.spot.domain.review.dto.response.CompletedJobReview(" +
            "r.id, m.nickname, m.id, m.img, r.score, r.comment) " +
            "FROM Review r " +
            "JOIN Member m ON r.writerId = m.id " +
            "JOIN Matching mt ON mt.job = r.job " +
            "WHERE r.job = :jobId AND r.targetId = :targetId " +
            "ORDER BY COALESCE(r.updatedAt, r.createdAt) DESC")
    List<CompletedJobReview> findAllByJobIdAndTargetId(@Param("jobId") Long jobId, @Param("targetId") Long targetId);

    @Query(value = """
        SELECT
            r.id AS review_id,
            m.nickname AS writer_nickname,
            m.id AS writer_id,
            m.img AS writer_img,
            r.score,
            r.comment
        FROM review r
        JOIN members m ON r.writer_id = m.id
        JOIN Matching mt ON mt.job_id = r.job_id
        WHERE r.job_id = :jobId AND r.target_id = :targetId
        """, nativeQuery = true)
    List<Object[]> findAllByJobIdAndTargetId1(@Param("jobId") Long jobId, @Param("targetId") Long targetId);
}
