package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import spot.spot.domain.job.entity.Job;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KlayAboutJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "amt_klay", nullable = false)
    private double amtKlay;

    @Column(name = "amt_krw" , nullable = false)
    private int amtKrw;

    @Column(name = "exchange_rate", nullable = false)
    private double exchangeRate;

}
