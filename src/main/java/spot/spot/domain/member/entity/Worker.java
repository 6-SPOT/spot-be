package spot.spot.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.global.auditing.entitiy.Created;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Worker extends Created {
    @Id
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    private String introduction;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isWorking;  // 0 = 일 중, 1 = 일 잠시 쉼

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerAbility> workerAbilities;
}
