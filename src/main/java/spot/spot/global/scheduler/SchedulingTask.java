package spot.spot.global.scheduler;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;

// runnable을 구현한 클래스는 스레드에서 실행될 수 있는 작업이됨.
// threadPoolTashScheduler에게 이 구현체의 객체를 넘기면 특정 시간 후 실행됨
@AllArgsConstructor
public class SchedulingTask<T> implements Runnable{

    private final T target;                                 // 작업 실행 시 사용하는 객체
    private final Consumer<T> task;                         // 실행할 작업 자체
    private final TransactionTemplate transactionTemplate;  // 트랜잭션 관리 도구 - 트랜잭션을 직접 제어 및 관리할 수 있게 도와줌


    @Override
    public void run() {
        transactionTemplate.execute(status -> { // 이 템플릿 안에서만 트랜잭션 적용되어 영속성을 유지함.
            task.accept(target);                // 특정 에러 발생시 작업을 롤백, 데이터 손상 방지
            return null;
        });
    }
}
