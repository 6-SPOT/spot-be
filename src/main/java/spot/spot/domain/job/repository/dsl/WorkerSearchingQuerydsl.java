package spot.spot.domain.job.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import spot.spot.domain.job.entity.MatchingStatus;
import spot.spot.domain.job.entity.QMatching;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.member.entity.QWorker;
import spot.spot.domain.member.entity.Worker;

@Repository
@RequiredArgsConstructor
public class WorkerSearchingQuerydsl {
    private final JPAQueryFactory queryFactory;
    private final QWorker worker = QWorker.worker;
    private final QMatching matching = QMatching.matching;
    private final QMember member = QMember.member;

    public Slice<Worker> findWorkersByJobIdAndStatus(Long jobId, Pageable pageable) {
        List<Worker> workers = queryFactory
            .selectFrom(worker)
            .join(worker.member, member).fetchJoin()
            .join(matching).on(matching.member.eq(member))
            .where(
                matching.job.id.eq(jobId),
                matching.status.eq(MatchingStatus.ATTENDER)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // Slice 처리를 위해 +1
            .fetch();

        boolean hasNext = workers.size() > pageable.getPageSize();
        if (hasNext) {
            workers.remove(workers.size() - 1); // Slice에서 마지막 요소 제거
        }

        return new SliceImpl<>(workers, pageable, hasNext);
    }
}
