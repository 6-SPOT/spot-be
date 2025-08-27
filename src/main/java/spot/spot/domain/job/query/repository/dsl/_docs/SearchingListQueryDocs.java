package spot.spot.domain.job.query.repository.dsl._docs;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import spot.spot.domain.job.command.dto.response.JobSituationResponse;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.command.entity.MatchingStatus;
import spot.spot.domain.job.command.entity.QJob;
import spot.spot.domain.job.command.entity.QMatching;
import spot.spot.domain.job.query.dto.response.CertificationImgResponse;
import spot.spot.domain.job.query.dto.response.NearByJobResponse;
import spot.spot.domain.member.entity.Worker;

public interface SearchingListQueryDocs {
    // QueryDSL로 해결사 근처 일 리스트 찾기 - 지도에 띄우는 걸 가정한 반경 사용자 좌표 변환
    public Slice<NearByJobResponse> findNearByJobsWithQueryDSL(double lat, double lng, double dist, Pageable pageable);
    // 일에 신청한 해결사 리스트 반환
    public Slice<Worker> findWorkersByJobId(Long jobId, Pageable pageable);
    // 일 주인이 봤을 때 일 의뢰 현황 리스트 반환
    public List<JobSituationResponse> findJobSituationsByOwner(long memberId);
    // 해결사가 봤을 때, 맡은 일의 현황 리스트 반환
    public List<JobSituationResponse> findJobSituationsByWorker(long memberId);
    // 해결사의 인증 사진 리스트 반환
    public List<CertificationImgResponse> findWorkersCertificationImgList(long jobId);
}
