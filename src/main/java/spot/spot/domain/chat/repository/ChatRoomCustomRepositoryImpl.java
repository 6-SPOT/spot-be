package spot.spot.domain.chat.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.entity.QChatParticipant;
import spot.spot.domain.chat.entity.QChatRoom;

@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Long> findChatRoomId(Long member1Id, Long member2Id, Long jobId) {
		QChatRoom chatRoom = QChatRoom.chatRoom;
		QChatParticipant participant = QChatParticipant.chatParticipant;

		return Optional.ofNullable(
			queryFactory
				.select(chatRoom.id)
				.from(chatRoom)
				.join(participant).on(participant.chatRoom.eq(chatRoom))
				.where(
					chatRoom.job.id.eq(jobId),
					participant.member.id.in(member1Id, member2Id)
				)
				.groupBy(chatRoom.id)
				.having(participant.count().eq(2L))
				.fetchFirst()
		);
	}
}
