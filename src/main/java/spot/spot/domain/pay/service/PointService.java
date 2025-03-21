package spot.spot.domain.pay.service;

import jakarta.validation.constraints.NotBlank;
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
import spot.spot.domain.pay.repository.PointRepositoryDsl;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PointService {

    private final PointRepository pointRepository;
    private final MemberService memberService;
    private final PointRepositoryDsl pointRepositoryDsl;

    //포인트 쿠폰 등록
//    public List<PointServeResponseDto> servePoint(List<PointServeRequestDto> requestDto) {
//        List<PointServeResponseDto> responseDtos = new ArrayList<>();
//        List<Point> pointList = requestDto.stream().map(
//                req -> new AbstractMap.SimpleEntry<>(req, UUID.randomUUID().toString().substring(0, 6))
//        ).flatMap(entry -> {
//            PointServeRequestDto req = entry.getKey();
//            String pointCode = entry.getValue();
//
//            Stream<Point> pointStream = IntStream.range(0, req.count())
//                    .mapToObj(i -> PointServeRequestDto.toPoint(req, pointCode));
//            responseDtos.add(new PointServeResponseDto(req.pointName(), req.point(), pointCode, req.count()));
//            return pointStream;
//        }).collect(Collectors.toList());
//
//        pointRepository.saveAll(pointList);
//
//        return responseDtos;
//    }

    public List<PointServeResponseDto> servePoint(List<PointServeRequestDto> requestDto) {
        List<PointServeResponseDto> responseDtos = new ArrayList<>();
        List<Point> pointList = requestDto.stream().map(
                req -> {
                    String pointCode = UUID.randomUUID().toString().substring(0, 6);
                    responseDtos.add(new PointServeResponseDto(req.pointName(), req.point(), pointCode, req.count()));
                    return PointServeRequestDto.toPoint(req, pointCode);
        }).collect(Collectors.toList());

        pointRepository.saveAll(pointList);

        return responseDtos;
    }

    //쿠폰 사용
    public void registerPoint(String pointCode, String memberId) {
        Point validPoint = pointRepository.findByPointCode(pointCode)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPTY_POINT));
        decreasePointCount(validPoint);

        Member findMember = memberService.findById(memberId);
        int point = findMember.getPoint();
        findMember.setPoint(point + validPoint.getPoint());
    }

    public void registerPointWithOptimisticLock(String pointCode, String memberId) {
        for (int i = 0; i < 3; i++) {
            Point point = pointRepository.findByPointCode(pointCode)
                    .orElseThrow(() -> new RuntimeException("포인트 정보를 찾을 수 없습니다."));

            int oldCount = point.getCount();
            if(oldCount <= 0) throw new GlobalException(ErrorCode.INVALID_POINT_COUNT);

            int newCount = oldCount - 1;

            int updatedRows = pointRepositoryDsl.updatePointOptimistic(pointCode, oldCount, newCount);

            if (updatedRows > 0) {
                return;
            }
        }
        throw new RuntimeException("포인트 업데이트 중 충돌이 발생했습니다. 다시 시도해주세요.");
    }

    public void decreasePointCount(Point point) {
        if(point.getCount() <= 0) {
            throw new GlobalException(ErrorCode.INVALID_POINT_COUNT);
        }
        point.setCount(point.getCount() - 1);
    }

    //포인트코드가 같은 포인트 전체 삭제
    public void deletePoint(String pointCode) {
        validatePointCode(pointCode);
        pointRepository.deleteByPointCode(pointCode);
    }

    private void validatePointCode(String pointCode) {
        pointRepository.findByPointCode(pointCode).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_POINT_CODE));
        if (pointCode == null || pointCode.isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_POINT_CODE);
        }
    }

    public PointServeResponseDto findByPointName(String pointName) {
        Point point = pointRepository.findByPointName(pointName).orElseThrow(() -> new GlobalException(ErrorCode.INVALID_POINT_NAME));
        return PointServeResponseDto.fromPoint(point);
    }
}
