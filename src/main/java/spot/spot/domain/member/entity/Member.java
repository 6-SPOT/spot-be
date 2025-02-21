package spot.spot.domain.member.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import java.time.LocalDateTime;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.member.entity.dto.MemberRole;
import spot.spot.domain.notification.entity.FcmToken;
import spot.spot.domain.notification.entity.Notification;
import spot.spot.global.auditing.entitiy.Deleted;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends Deleted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String email;

    @Setter
    private String nickname;

    @Setter
    private String img;

    @Setter
    private String phone;

    @Setter
    private double lat; //위도

    @Setter
    private double lng; //경도

    @Setter
    private int point;

    private MemberRole memberRole;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Worker worker;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchingList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokenList;

}

