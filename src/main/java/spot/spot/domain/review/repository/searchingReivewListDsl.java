package spot.spot.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import spot.spot.domain.member.entity.QMember;
import spot.spot.domain.review.dto.response.AuthoredReview;
import spot.spot.domain.review.dto.response.ReceivedReview;
import spot.spot.domain.review.entity.QReview;

@Repository
@RequiredArgsConstructor
public class searchingReivewListDsl {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QMember member = QMember.member;

    public Slice<ReceivedReview> getReviewListByReceiver(long targetId, Pageable pageable) {
        List<ReceivedReview> reviewList = queryFactory
            .select(
                Projections.constructor(
                    ReceivedReview.class,
                    review.id,
                    member.nickname,
                    review.writerId,
                    member.img,
                    review.score,
                    review.comment
                )
            )
            .from(review)
            .innerJoin(member)
            .on(review.targetId.eq(member.id))
            .where(review.targetId.eq(targetId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = reviewList.size() > pageable.getPageSize();
        if (hasNext) reviewList = reviewList.subList(0, pageable.getPageSize());

        return new SliceImpl<>(reviewList, pageable, hasNext);
    }

    public Slice<AuthoredReview> getReviewListByAuthor(long writerId, Pageable pageable) {
        List<AuthoredReview> reviewList = queryFactory
            .select(
                Projections.constructor(
                    AuthoredReview.class,
                    review.id,
                    member.nickname,
                    review.targetId,
                    member.img,
                    review.score,
                    review.comment
                )
            )
            .from(review)
            .innerJoin(member)
            .on(review.targetId.eq(member.id))
            .where(review.targetId.eq(writerId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = reviewList.size() > pageable.getPageSize();
        if (hasNext) reviewList = reviewList.subList(0, pageable.getPageSize());

        return new SliceImpl<>(reviewList, pageable, hasNext);
    }



}
