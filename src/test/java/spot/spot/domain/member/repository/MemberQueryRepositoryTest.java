package spot.spot.domain.member.repository;

import jakarta.persistence.Transient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.MemberRole;
import spot.spot.domain.pay.entity.Point;
import spot.spot.domain.pay.repository.PointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MemberQueryRepositoryTest {

    @Autowired
    PointRepository pointRepository;

    @Autowired
    MemberRepository memberRepository;

    @Transient  // 테스트에서만 임시 주석처리 가능
    private Point location;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Test
    void updatePointByRegister() {
        ///given
        Point point = Point.create("pointName", 1000, "pointCode", 1);
        Point savePoint = pointRepository.save(point);

        GeometryFactory geometryFactory = new GeometryFactory();
        org.locationtech.jts.geom.Point testPoint = geometryFactory.createPoint(new Coordinate(127.027619, 37.497942)); // (lng, lat)
        testPoint.setSRID(4326);

        Member member = Member.builder()
                .nickname("test")
                .email("test@test.com")
                .memberRole(MemberRole.MEMBER)
                .point(0)
                .lat(37.497942)
                .lng(127.027619)
                .location(testPoint)  // 필수!
                .build();

        Member saveMember = memberRepository.save(member);

        ///when
        Optional<Member> resultMember = memberQueryRepository.updatePointByRegister(saveMember.getId(), savePoint.getPointCode());

        ///then
        Assertions.assertThat(resultMember.get().getPoint()).isEqualTo(1000);
    }

    @Test
    void insertMember100() {
        GeometryFactory geometryFactory = new GeometryFactory();
        org.locationtech.jts.geom.Point testPoint = geometryFactory.createPoint(new Coordinate(127.027619, 37.497942)); // (lng, lat)
        testPoint.setSRID(4326);
        List<Member> memberList = new ArrayList<>();

        for(int i=1; i<=100; i++) {
            Member member = Member.builder()
                    .nickname("test" + i)
                    .email("test" + i + "@test.com")
                    .memberRole(MemberRole.MEMBER)
                    .point(0)
                    .lat(37.497942)
                    .lng(127.027619)
                    .location(testPoint)  // 필수!
                    .build();

            memberList.add(member);
        }

        memberRepository.saveAll(memberList);
    }
}