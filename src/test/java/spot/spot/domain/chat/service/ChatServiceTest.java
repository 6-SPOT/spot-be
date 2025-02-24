package spot.spot.domain.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.domain.chat.dto.request.ChatRoomCreateRequest;
import spot.spot.domain.chat.dto.response.ChatListResponse;
import spot.spot.domain.chat.dto.response.ChatMessageResponse;
import spot.spot.domain.chat.entity.ChatMessage;
import spot.spot.domain.chat.entity.ChatParticipant;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.chat.entity.ReadStatus;
import spot.spot.domain.chat.repository.ChatMessageRepository;
import spot.spot.domain.chat.repository.ChatParticipantRepository;
import spot.spot.domain.chat.repository.ChatRoomRepository;
import spot.spot.domain.chat.repository.ReadStatusRepository;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.job.repository.JobRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.global.common.TestUtils;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

	@InjectMocks
	private ChatService chatService;

	@Mock
	private ChatRoomRepository chatRoomRepository;
	@Mock
	private ChatParticipantRepository chatParticipantRepository;
	@Mock
	private ChatMessageRepository chatMessageRepository;
	@Mock
	private ReadStatusRepository readStatusRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private JobRepository jobRepository;

	@DisplayName("메세지 저장 테스트")
	@Test
	void saveMessage() {
		// given
		Long roomId = 1L;
		Long memberId = 1L;

		Member sender = TestUtils.createMember();
		Member otherMember = TestUtils.createMember();
		Job job = TestUtils.createJob();
		ChatRoom chatRoom = TestUtils.createChatRoom(job);
		ChatMessageCreateRequest chatMessageCreateRequest = ChatMessageCreateRequest.builder()
			.content("content")
			.build();

		ChatParticipant senderParticipant = TestUtils.chatParticipant(sender, chatRoom);
		ChatParticipant otherParticipant = TestUtils.chatParticipant(otherMember, chatRoom);

		List<ChatParticipant> chatParticipants = Arrays.asList(senderParticipant, otherParticipant);

		when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
		when(memberRepository.findById(roomId)).thenReturn(Optional.of(sender));
		when(chatParticipantRepository.findByChatRoom(chatRoom)).thenReturn(chatParticipants);
		// when
		chatService.saveMessage(roomId, chatMessageCreateRequest, memberId);

		// then
		verify(chatMessageRepository).save(any(ChatMessage.class));
		verify(readStatusRepository, times(2)).save(any(ReadStatus.class));

	}

	@DisplayName("이전 메시지 가져오기")
	@Test
	void getChatHistory() {

		// given
		Long roomId = 1L;
		Long memberId = 1L;

		Job job = TestUtils.createJob();
		ChatRoom chatRoom = TestUtils.createChatRoom(job);
		Member member = TestUtils.createMember();
		ChatParticipant chatParticipant = TestUtils.chatParticipant(member, chatRoom);

		ChatMessage message1 = ChatMessage.builder()
			.content("test1")
			.member(member)
			.chatRoom(chatRoom)
			.build();

		ChatMessage message2 = ChatMessage.builder()
			.content("test2")
			.member(member)
			.chatRoom(chatRoom)
			.build();

		when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(chatParticipantRepository.findByChatRoom(chatRoom)).thenReturn(List.of(chatParticipant));
		when(chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom))
			.thenReturn(List.of(message1, message2));

		// when

		List<ChatMessageResponse> chatMessageResponseList = chatService.getChatHistory(roomId, memberId);

		// then
		assertThat(chatMessageResponseList).hasSize(2);
		assertThat(chatMessageResponseList)
			.extracting("content")
			.containsExactlyInAnyOrder("test1", "test2");
	}
	@DisplayName("내 채팅방 목록 가져오기")
	@Test
	void getMyChatRooms() {
	    // given
		Long memberId = 1L;

		Job job1 = TestUtils.createJob();
		Job job2 = TestUtils.createJob();

		ChatRoom chatRoom1 = TestUtils.createChatRoom(job1);
		ChatRoom chatRoom2 = TestUtils.createChatRoom(job2);

		Member member = TestUtils.createMember();
		ChatParticipant chatParticipant1 = TestUtils.chatParticipant(member, chatRoom1);
		ChatParticipant chatParticipant2 = TestUtils.chatParticipant(member, chatRoom2);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(chatParticipantRepository.findAllByMember(member))
			.thenReturn(Arrays.asList(chatParticipant1, chatParticipant2));
		when(readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(chatRoom1, member)).thenReturn(3L);
		when(readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(chatRoom2, member)).thenReturn(0L);

		// when
		List<ChatListResponse> result = chatService.getMyChatRooms(memberId);

		// then
		assertThat(result).hasSize(2);
		assertThat(result)
			.extracting("title", "unReadCount")
				.containsExactlyInAnyOrder(
					tuple("title", 3L),
					tuple("title", 0L)
				);
	}

	@DisplayName("채팅방이 존재하는 경우")
	@Test
	void getOrCreateChatRoomAlreadyExist() {
		Long memberId = 1L;
		Long otherMemberId = 2L;
		Long jobId = 1L;
		Long existingRoomId = 5L;

		Job job = TestUtils.createJob();
		Member member = TestUtils.createMember();
		Member otherMember = TestUtils.createMember();

		ChatRoomCreateRequest request = new ChatRoomCreateRequest(jobId, otherMemberId);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(memberRepository.findById(otherMemberId)).thenReturn(Optional.of(otherMember));
		when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
		when(chatRoomRepository.findChatRoomId(eq(memberId), any(), any()))
			.thenReturn(Optional.of(existingRoomId));

		// when
		Long result = chatService.getOrCreateChatRoom(request, memberId);

		// then
		assertThat(result).isEqualTo(existingRoomId);
		verify(chatRoomRepository, never()).save(any());
		verify(chatParticipantRepository, never()).save(any());
	}

	@Test
	@DisplayName("채팅방이 없는 경우 새로운 채팅방을 생성한다")
	void getOrCreateChatRoomCreateNewRoom() {
		// given
		Long memberId = 1L;
		Long otherMemberId = 2L;
		Long jobId = 1L;
		Long newRoomId = 5L;

		ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
			.jobId(jobId)
			.otherMemberId(otherMemberId)
			.build();

		Job job = TestUtils.createJob();
		Job spyJob = spy(job);
		Member member = TestUtils.createMember();
		Member otherMember = TestUtils.createMember();

		ChatRoom newChatRoom = TestUtils.createChatRoom(job);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(memberRepository.findById(otherMemberId)).thenReturn(Optional.of(otherMember));
		when(jobRepository.findById(jobId)).thenReturn(Optional.of(spyJob));
		when(chatRoomRepository.findChatRoomId(eq(memberId), any(), any()))
			.thenReturn(Optional.empty());
		when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(newChatRoom);
		doReturn(jobId).when(spyJob).getId();

		// when
		Long result = chatService.getOrCreateChatRoom(request, memberId);

		// then
		// assertThat(result).isEqualTo(newRoomId);
		verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
		verify(chatParticipantRepository, times(2)).save(any());

	}
}
