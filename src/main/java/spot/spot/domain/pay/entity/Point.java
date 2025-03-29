package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_name", nullable = false)
    private String pointName;

    @Column(name = "point", nullable = false)
    private int point;

    @Column(name = "point_code", nullable = false)
    private String pointCode;

    @Column(name = "count", nullable = false)
    @Setter
    private int count;

    @Enumerated(EnumType.STRING)
    private PointStatus pointStatus;

    @Builder
    private Point(String pointName, int point, String pointCode, int count) {
        this.pointName = pointName;
        this.point = point;
        this.pointCode = pointCode;
        this.pointStatus = PointStatus.VALID;
        this.count = count;
    }

    public static Point create(String pointName, int point, String pointCode, int count) {
        return Point.builder()
                .pointName(pointName)
                .point(point)
                .pointCode(pointCode)
                .count(count)
                .build();
    }
}
