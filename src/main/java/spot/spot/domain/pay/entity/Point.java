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


    @Builder
    private Point(String pointName, int point, String pointCode, int count) {
        this.pointName = pointName;
        this.point = point;
        this.pointCode = pointCode;
        this.count = count;
    }
}
