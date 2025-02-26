package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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

    @Setter
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    private LocalDateTime createAt;
}
