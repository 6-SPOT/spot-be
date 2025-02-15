package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
