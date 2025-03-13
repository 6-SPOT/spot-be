package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;
import spot.spot.domain.job.command.entity.Job;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KlayAboutJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "amt_klay", nullable = false)
    private double amtKlay;

    @Column(name = "amt_krw" , nullable = false)
    private int amtKrw;

    @Column(name = "exchange_rate", nullable = false)
    private double exchangeRate;

    @Builder
    private KlayAboutJob(Job job, double amtKlay, int amtKrw, double exchangeRate) {
        this.job = job;
        this.amtKlay = amtKlay;
        this.amtKrw = amtKrw;
        this.exchangeRate = exchangeRate;
    }
}
