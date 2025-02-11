package spot.spot.pay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int payAmount;
    private int payPoint;
    private String depositor;
    private Long memberId;
}
