package spot.spot.domain.job.query.service._docs;

import spot.spot.domain.job.query.dto.request.PositionRequest;
import spot.spot.domain.job.query.dto.response.PositionResponse;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

public interface PositionQueryServiceDocs {
    // 실시간 해결사 위치 확인
    public void sendPosition (PositionRequest request, String atk);
}
