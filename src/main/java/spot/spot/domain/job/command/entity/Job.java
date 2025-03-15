package spot.spot.domain.job.command.entity;

import com.google.api.client.json.JsonPolymorphicTypeMap.TypeDef;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import spot.spot.domain.review.entity.Review;
import spot.spot.global.auditing.entitiy.CreatedAndDeleted;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Job extends CreatedAndDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("PK: 일의 아이디")
    private Long id;

    private Double lat;
    private Double lng;

    // Point = MySQL의 공간 데이터 타입 중 하나, SRID = 공간 데이터에서 사용하는 좌표계 ID, 우리는 4326 즉 WGS 84 - 전 세계적인 GPS 시스템에 활용하는 로직을 쓴다.
    @Column(columnDefinition = "POINT SRID 4326", nullable = false)
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Point location;


    private String title;
    private String content;
    private Integer money;

    @Column(length =  1000, nullable = true)
    private String img;

    @Setter
    private String tid;

    private LocalDateTime startedAt;

    // 연관 관계 (주인 섦정: job (job에서의 조회는 읽기 전용), 영속성 전이 설정, 연관관계가 끊기면 고아가 된 레코드 삭제, FetchType.Lazy 설정)
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matching> matchings;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobHashTag> jobHashTagList;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> Review;
}
