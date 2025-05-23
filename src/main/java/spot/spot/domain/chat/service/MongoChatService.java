package spot.spot.domain.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import spot.spot.domain.chat.dto.request.ChatMessageCreateRequest;
import spot.spot.domain.chat.dto.request.ChatRoomCreateRequest;
import spot.spot.domain.chat.dto.response.ChatListResponse;
import spot.spot.domain.chat.dto.response.ChatMessageResponse;
import spot.spot.domain.chat.entity.ChatMessage;
import spot.spot.domain.chat.entity.ChatParticipant;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.chat.entity.ReadStatus;
import spot.spot.domain.chat.mongodb.MongoChatMessage;
import spot.spot.domain.chat.repository.ChatMessageRepository;
import spot.spot.domain.chat.repository.ChatParticipantRepository;
import spot.spot.domain.chat.repository.ChatRoomRepository;
import spot.spot.domain.chat.repository.MongoChatMessageRepository;
import spot.spot.domain.chat.repository.ReadStatusRepository;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.domain.job.query.repository.jpa.JobRepository;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Profile("kafka")
public class MongoChatService implements ChatService{

	private final ChatRoomRepository chatRoomRepository;
	private final ChatParticipantRepository chatParticipantRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MemberRepository memberRepository;
	private final JobRepository jobRepository;
	private final MongoChatMessageRepository mongoChatMessageRepository;


	@Transactional
	public ChatMessageResponse saveMessage(Long roomId, ChatMessageCreateRequest chatMessageDto, Long memberId) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new EntityNotFoundException("room cannot find")
		);

		Member sender = memberRepository.findById(memberId).orElseThrow(
			() -> new EntityNotFoundException("member cannot find")
		);

		MongoChatMessage mongoChatMessage = MongoChatMessage.builder()
			.chatRoomId(chatRoom.getId())
			.senderId(sender.getId())
			.senderNickName(sender.getNickname())
			.content(chatMessageDto.content())
			.build();
		mongoChatMessageRepository.save(mongoChatMessage);

		return ChatMessageResponse.builder()
			.senderId(sender.getId())
			.senderNickname(sender.getNickname())
			.content(chatMessageDto.content())
			.build();
	}

	// 이전 메시지 가져오기
	public List<ChatMessageResponse> getChatHistory(Long roomId, Long memberId) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new EntityNotFoundException("room not found"));

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("member not found"));

		List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(chatRoom);
		// 채팅방 참여자인지 체크
		boolean isRoomMember = participants.stream()
			.anyMatch(chatParticipant -> chatParticipant.getMember().equals(member));
		if (!isRoomMember) {
			throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
		}

		List<MongoChatMessage> chatMessages = mongoChatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(
			chatRoom.getId());
		return new ArrayList<>(chatMessages.stream()
			.map(chatMessage -> ChatMessageResponse.builder()
				.content(chatMessage.getContent())
				.senderId(chatMessage.getSenderId())
				.senderNickname(chatMessage.getSenderNickName())
				.build())
			.toList());
	}

	// 메시지 읽음 처리
	@Transactional
	public void messageRead(Long roomId, Long memberId) {
		ChatRoom chatRoom = chatRoomRepository.findById(roomId)
			.orElseThrow(() -> new EntityNotFoundException("room not found"));

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("member not found"));
		List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
		readStatuses.forEach(readStatus -> readStatus.setIsRead(true));
	}

	// 내 채팅방 목록 가져오기
	public List<ChatListResponse> getMyChatRooms(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("member not found"));
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
		return new ArrayList<>(chatParticipants.stream()
			.map(c -> {
					Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
					return ChatListResponse.builder()
						.roomId(c.getChatRoom().getId())
						.title(c.getChatRoom().getTitle())
						.unReadCount(count)
						.build();
				}
			).toList());
	}

	@Transactional
	public Long getOrCreateChatRoom(ChatRoomCreateRequest chatRoomCreateRequest, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("member not found"));

		Member otherMember = memberRepository.findById(chatRoomCreateRequest.otherMemberId())
			.orElseThrow(() -> new EntityNotFoundException("member not found"));

		Job job = jobRepository.findById(chatRoomCreateRequest.jobId())
			.orElseThrow(() -> new EntityNotFoundException("job not found"));

		Optional<Long> chatRoomId = chatRoomRepository.findChatRoomId(memberId, otherMember.getId(), job.getId());
		if (chatRoomId.isPresent()) {
			return chatRoomId.get();
		}
		// 생성
		ChatRoom chatRoom = ChatRoom.builder()
			.job(job)
			.title(job.getTitle() + " : " + member.getNickname() + " - " + otherMember.getNickname())
			.createdAt(LocalDateTime.now())
			.build();
		ChatRoom saved = chatRoomRepository.save(chatRoom);

		addParticipantToRoom(saved, member);
		addParticipantToRoom(saved, otherMember);
		return saved.getId();
	}

	private void addParticipantToRoom(ChatRoom chatRoom, Member member) {
		ChatParticipant chatParticipant = ChatParticipant.builder()
			.chatRoom(chatRoom)
			.member(member)
			.build();
		chatParticipantRepository.save(chatParticipant);
	}


}
