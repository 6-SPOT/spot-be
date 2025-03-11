package spot.spot.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;
import spot.spot.domain.job.command.entity.Job;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Setter
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    private LocalDateTime createAt = LocalDateTime.now();

    @Builder
    private PayHistory(int payAmount, int payPoint, String depositor, String worker, PayStatus payStatus, Job job) {
        this.payAmount = payAmount;
        this.payPoint = payPoint;
        this.depositor = depositor;
        this.worker = worker;
        this.payStatus = payStatus;
        this.job = job;
    }

    public static PayHistory create(int payAmount, int payPoint, String depositor, String worker, PayStatus payStatus, Job job) {
        return PayHistory.builder()
                .payAmount(payAmount)
                .payPoint(payPoint)
                .payStatus(payStatus)
                .depositor(depositor)
                .worker(worker)
                .job(job)
                .build();
    }

}
