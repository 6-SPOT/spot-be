package spot.spot.domain.member.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import spot.spot.domain.job.entity.Matching;
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

    private int point;

    private LocalDateTime deletedAt; //삭제일자

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchingList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokenList;

    /*

    private OAuthProvider oAuthProvider; //소셜로그인

    public Member(String email,String nickname,OAuthProvider oAuthProvider){
        this.email = email;
        this.nickname = nickname;
        this.oAuthProvider = oAuthProvider;
    }

     */

}

