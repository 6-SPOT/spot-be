package spot.spot.domain.review.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //리뷰 아이디

    @Column(nullable = false)
    private Long jobId; //일의 아이디

    @Column(nullable = false)
    private Long writerId; //평가자 아이디 => 토큰으로 식별

    @Column(nullable = false)
    private Long targetId; //피평가자

    @Column(nullable = false)
    private Integer score; //평점

    @Column(length = 300)
    private String comment; //리뷰 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; //생성일자

    private LocalDateTime deletedAt; //생성일자

    @PrePersist  // 시간 동기화
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}