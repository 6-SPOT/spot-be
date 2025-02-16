package spot.spot.domain.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.member.entity.Member;
import spot.spot.global.auditing.entitiy.Updated;

@Entity
@Table(name = "fcm_token")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken extends Updated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
