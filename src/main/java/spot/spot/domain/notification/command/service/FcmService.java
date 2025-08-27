package spot.spot.domain.notification.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.spot.domain.job.command.mapper.NotificationMapper;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.member.repository.MemberRepository;
import spot.spot.domain.notification.command.dto.request.FcmTestRequest;
import spot.spot.domain.notification.command.dto.request.UpdateFcmTokenRequest;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.entity.FcmToken;
import spot.spot.domain.notification.command.entity.NoticeType;
import spot.spot.domain.notification.command.entity.Notification;
import spot.spot.domain.notification.command.repository.FcmTokenRepository;
import spot.spot.domain.notification.command.repository.NotificationRepository;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.security.util.UserAccessUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserAccessUtil userAccessUtil;
    private final FcmAsyncSendingUtil fcmAsyncSendingUtil;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public void saveFcmToken(UpdateFcmTokenRequest request) {
        Member member = userAccessUtil.getMember();
        fcmTokenRepository.findByMemberAndData(member, request.fcmToken())
            .orElseGet(() -> fcmTokenRepository.save(FcmToken.builder()
                .member(member)
                .data(request.fcmToken())
                .build()));
    }

    @Transactional
    public void testSending(FcmTestRequest request) {
        Member receiver = memberRepository.findById(request.receiver_id())
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));
        log.info("test sending 보내는 중---- id: {}, 내용물: {}", request.receiver_id(), request.content());
        FcmDTO msg = FcmDTO.builder().title(String.valueOf(receiver.getId())).body(request.content()).build();
        fcmAsyncSendingUtil.singleFcmSend(receiver.getId(), msg);

        Notification noti = notificationMapper.toNotification(msg, NoticeType.JOB, userAccessUtil.getMember(),
            request.receiver_id());
        log.info("{}, {}", noti.getContent(), noti.getReceiverId());
        notificationRepository.save(noti);
    }
}