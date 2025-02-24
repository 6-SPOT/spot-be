package spot.spot.domain.chat.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jnr.a64asm.Mem;
import spot.spot.domain.chat.entity.ChatParticipant;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.repository.JobRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.auditing.config.JpaAuditingConfig;
import spot.spot.global.common.TestUtils;
import spot.spot.global.config.QuerydslConfig;

@DataJpaTest
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
class ChatRoomRepositoryTest {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatParticipantRepository chatParticipantRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Member savedMember;
	private Member savedOtherMember;
	private Job savedJob;
	private ChatRoom savedChatRoom;

	@BeforeEach
	void setUp() {
		Member member = TestUtils.createMember();
		savedMember = memberRepository.save(member);

		Member otherMember = TestUtils.createMember();
		savedOtherMember = memberRepository.save(otherMember);

		Job job = TestUtils.createJob();
		savedJob = jobRepository.save(job);

		ChatRoom chatRoom = TestUtils.createChatRoom(job);
		savedChatRoom = chatRoomRepository.save(chatRoom);

		ChatParticipant chatParticipant1 = TestUtils.chatParticipant(member, chatRoom);
		ChatParticipant chatParticipant2 = TestUtils.chatParticipant(member, chatRoom);
		chatParticipantRepository.save(chatParticipant1);
		chatParticipantRepository.save(chatParticipant2);
	}

	@DisplayName("채팅방을 가져온다")
	@Test
	void findChatRoomId() {
		Optional<Long> chatRoomId = chatRoomRepository.findChatRoomId(savedMember.getId(), savedOtherMember.getId(), savedJob.getId());
		assertThat(chatRoomId).isPresent().hasValue(savedChatRoom.getId());

	}

}
