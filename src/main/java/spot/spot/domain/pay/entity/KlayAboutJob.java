package spot.spot.domain.pay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KlayAboutJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amtKlay;

    private int amtKRW;

    private double exchangeRate;
}
