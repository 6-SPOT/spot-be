package spot.spot.domain.pay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.service.MemberService;
import spot.spot.domain.pay.entity.Point;
import spot.spot.domain.pay.entity.dto.request.PointServeRequestDto;
import spot.spot.domain.pay.entity.dto.response.PointServeResponseDto;
import spot.spot.domain.pay.repository.PointRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointRepository pointRepository;
    private final MemberService memberService;

    //포인트 쿠폰 등록
    public List<PointServeResponseDto> servePoint(List<PointServeRequestDto> requestDto) {
        List<PointServeResponseDto> responseDtos = new ArrayList<>();
        List<Point> pointList = requestDto.stream().map(
                req -> new AbstractMap.SimpleEntry<>(req, UUID.randomUUID().toString().substring(0, 6))
        ).flatMap(entry -> {
            PointServeRequestDto req = entry.getKey();
            String pointCode = entry.getValue();

            if (req.pointName() == null || req.pointName().isEmpty()) {
                throw new GlobalException(ErrorCode.EMPTY_POINT_NAME);
            }else if(req.point() <= 0) {
                throw new GlobalException(ErrorCode.INVALID_POINT_AMOUNT);
            }else if(req.count() <= 0) {
                throw new GlobalException(ErrorCode.INVALID_POINT_COUNT);
            }

            Stream<Point> pointStream = IntStream.range(0, req.count())
                    .mapToObj(i -> Point.builder()
                            .pointName(req.pointName())
                            .point(req.point())
                            .pointCode(pointCode)
                            .isValid(true)
                            .build());
            responseDtos.add(new PointServeResponseDto(req.pointName(), req.point(), pointCode));
            return pointStream;
        }).collect(Collectors.toList());

        pointRepository.saveAll(pointList);

        return responseDtos;
    }

    //쿠폰 사용
    public void registerPoint(String pointCode,String memberId){
        validatePointCode(pointCode);
        Optional<Point> validPoint = pointRepository.findFirstByPointCodeAndIsValidTrue(pointCode);
        if (validPoint.isPresent()) {
            validPoint.get().setValid(false);
            Member findMember = memberService.findById(memberId);
            int point = findMember.getPoint();
            findMember.setPoint(point + validPoint.get().getPoint());
        } else {
            throw new GlobalException(ErrorCode.EMPTY_POINT);
        }
    }

    //쿠폰 하나삭제
    public void deletePointOnce(String pointCode) {
        validatePointCode(pointCode);
        Optional<Point> firstByPointCode = pointRepository.findFirstByPointCode(pointCode);
        if(firstByPointCode.isEmpty()) throw new GlobalException(ErrorCode.INVALID_POINT_CODE);
        firstByPointCode.ifPresent(pointRepository::delete);
    }

    //포인트코드가 같은 포인트 전체 삭제
    public void deletePoint(String pointCode) {
        validatePointCode(pointCode);
        pointRepository.deleteByPointCode(pointCode);
    }

    private void validatePointCode(String pointCode) {
        pointRepository.findFirstByPointCode(pointCode).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_POINT_CODE));
        if (pointCode == null || pointCode.isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_POINT_CODE);
        }
    }
}
