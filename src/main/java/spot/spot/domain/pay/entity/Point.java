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

    @Setter
    @Column(name = "is_valid", nullable = false)
    private boolean isValid;

    @Builder
    private Point(String pointName, int point, String pointCode, boolean isValid) {
        this.pointName = pointName;
        this.point = point;
        this.pointCode = pointCode;
        this.isValid = isValid;
    }
}
