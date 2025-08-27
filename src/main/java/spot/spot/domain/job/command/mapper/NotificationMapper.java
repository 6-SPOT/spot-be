package spot.spot.domain.job.command.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import spot.spot.domain.member.entity.Member;
import spot.spot.domain.notification.command.dto.response.FcmDTO;
import spot.spot.domain.notification.command.entity.NoticeType;
import spot.spot.domain.notification.command.entity.Notification;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", source = "sender")
    @Mapping(target = "content", source = "fcmDTO.body")
    @Mapping(target = "receiverId", source = "receiver_id")
    Notification toNotification(FcmDTO fcmDTO, NoticeType type, Member sender, Long receiver_id);
}
