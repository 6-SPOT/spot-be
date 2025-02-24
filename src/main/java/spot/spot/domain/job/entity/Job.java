package spot.spot.domain.job.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.global.auditing.entitiy.CreatedAndDeleted;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Job extends CreatedAndDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double lat;

    private Double lng;

    private String title;

    private String content;

    private Integer money;

    @Column(length =  1000)
    private String img;

    private String tid;

    private LocalDateTime startedAt;

    // 연관 관계 (주인 섦정: job (job에서의 조회는 읽기 전용), 영속성 전이 설정, 연관관계가 끊기면 고아가 된 레코드 삭제, FetchType.Lazy 설정)
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchings;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobHashTag> jobHashTagList;
}
