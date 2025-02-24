package spot.spot.global.common;

import java.time.LocalDateTime;

import spot.spot.domain.chat.entity.ChatParticipant;
import spot.spot.domain.chat.entity.ChatRoom;
import spot.spot.domain.job.entity.Job;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.entity.dto.MemberRole;

public class TestUtils {

	public static Member createMember() {
		return Member.builder()
			.email("test@naver.com")
			.nickname("nickname")
			.memberRole(MemberRole.MEMBER)
			.build();
	}

	public static Job createJob() {
		return Job.builder()
			.content("content")
			.title("title")
			.money(1111)
			.img("img")
			.build();
	}

	public static ChatRoom createChatRoom(Job job) {
		return ChatRoom.builder()
			.title("title")
			.thumbnailImageUrl("imgurl")
			.createdAt(LocalDateTime.now())
			.job(job)
			.build();
	}

	public static ChatParticipant chatParticipant(Member member, ChatRoom chatRoom) {
		return ChatParticipant.builder()
			.chatRoom(chatRoom)
			.member(member)
			.build();
	}
}
