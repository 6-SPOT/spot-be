package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pay_amount", nullable = false)
    private int payAmount;

    private int payPoint;

    @Column(name = "depositor", nullable = false)
    private String depositor;

    @Setter
    private String worker;

    private LocalDateTime createAt;
}
