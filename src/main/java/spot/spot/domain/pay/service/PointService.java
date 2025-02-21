package spot.spot.domain.pay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.Point;
import spot.spot.domain.pay.repository.PointRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PointService {

    private final PointRepository pointRepository;
    private final MemberService memberService;

    //포인트 쿠폰 등록
    public Point servePoint(String pointName, int point, String pointCode) {
        Point createPoint = Point.builder()
                .pointName(pointName)
                .point(point)
                .pointCode(pointCode)
                .isValid(true)
                .build();

        return pointRepository.save(createPoint);
    }

    //쿠폰 사용
    public void registerPoint(String pointCode,String memberId){
        Optional<Point> validPoint = pointRepository.findFirstByPointCodeAndIsValidTrue(pointCode);
        if (validPoint.isPresent()) {
            validPoint.get().setValid(false);
            Member findMember = memberService.findById(Long.parseLong(memberId));
            int point = findMember.getPoint();
            findMember.setPoint(point + validPoint.get().getPoint());
        } else {
            throw new GlobalException(ErrorCode.EMPTY_POINT);
        }
    }

    //쿠폰 삭제
    public void deletePoint(String pointCode) {
        pointRepository.deleteByPointCode(pointCode);
    }
}
