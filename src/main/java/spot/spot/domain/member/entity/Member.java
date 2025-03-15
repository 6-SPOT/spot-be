package spot.spot.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import spot.spot.domain.job.command.entity.Matching;
import spot.spot.domain.notification.command.entity.FcmToken;
import spot.spot.domain.notification.command.entity.Notification;

@Getter
@Entity
@NoArgsConstructor
@Table(name="members")
public class Member {

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

    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Point location;

    @Setter
    private int point;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Worker worker;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchingList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokenList;

    @Builder
    private Member(Long id,String email, String nickname, String img, String phone, double lat, double lng, int point, MemberRole memberRole, Worker worker, List<Matching> matchingList, List<Notification> notificationList, List<FcmToken> fcmTokenList) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.img = img;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.point = point;
        this.memberRole = memberRole;
        this.worker = worker;
        this.matchingList = matchingList;
        this.notificationList = notificationList;
        this.fcmTokenList = fcmTokenList;
    }
}

