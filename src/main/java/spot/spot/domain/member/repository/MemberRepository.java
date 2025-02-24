package spot.spot.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(@NotNull Long id);

    @Query(value = """
    SELECT  m.*,
            (6371*acos(
                    cos(radians(:lat)) * cos(radians(m.lat))
                    * cos(radians(m.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(m.lat)))) AS distance
    FROM member m
    INNER JOIN worker w ON m.id = w.member_id
    HAVING distance < :dist
    ORDER BY distance
    """, nativeQuery = true)
    List<Member> findWorkersNearByMember(@Param("lat") double lat, @Param("lng") double lng, @Param("dist") double dist);


    Optional<Member> findByNickname(String nickname);
}
