package spot.spot.domain.member.entity;

import jakarta.persistence.*;
import java.util.List;

import lombok.*;

import java.time.LocalDateTime;
import spot.spot.domain.job.entity.Matching;
import spot.spot.domain.member.entity.dto.MemberRole;
import spot.spot.domain.notification.entity.FcmToken;
import spot.spot.domain.notification.entity.Notification;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private double lat; //위도

    private double lng; //경도

    private String account;

    @Setter
    private int point;

    private MemberRole memberRole;

    private LocalDateTime deletedAt; //삭제일자

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchingList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokenList;

}

