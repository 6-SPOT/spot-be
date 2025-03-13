package spot.spot.domain.review.entity;


import jakarta.persistence.*;
import lombok.*;
import spot.spot.domain.job.command.entity.Job;
import spot.spot.global.auditing.entitiy.CreatedAndDeleted;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")

public class Review extends CreatedAndDeleted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //리뷰 아이디

    @Column(nullable = false)
    private Long writerId; //평가자 아이디 => 토큰으로 식별

    @Column(nullable = false)
    private Long targetId; //피평가자

    @Column(nullable = false)
    private Integer score; //평점

    @Column(length = 300)
    private String comment; //리뷰 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;
}