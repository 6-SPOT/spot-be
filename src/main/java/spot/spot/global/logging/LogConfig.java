package spot.spot.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;
import spot.spot.global.response.format.ResultResponse;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogConfig {
    @Around("@annotation(spot.spot.global.logging.Logging)")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        double beforeTime = System.currentTimeMillis();

        // request 파라미터 값 가져오기
        Object[] args = pjp.getArgs();
        StringBuilder logMsg = new StringBuilder();
        for (Object arg : args) {
            logMsg.append(arg.getClass().getSimpleName()).append(" [")
                .append(getObjectDetails(arg)).append("],  ");
        }
        if (logMsg.length() > 2) {
            logMsg.setLength(logMsg.length() - 2);
        }

        ColorLogger.green("-----------> REQUEST <Header>: {} \n <Body>: {}({}) ={}",
            getHeaderDetail(),
            pjp.getSignature().getDeclaringTypeName(),
            pjp.getSignature().getName(),
            logMsg);

        // 결과 확인
        Object result;
        try {
            result = pjp.proceed();  // AOP 실행
        } catch (Exception e) {
            log.error("다음의 메소드 실행 중 에러 발생: {}({})",
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(), e);
            throw e;  // 예외 재전파
        }

        double afterTime = System.currentTimeMillis();
        double executionTime = (afterTime - beforeTime) / 1000.0;

        // ResponseEntity일 경우 처리
        if (result instanceof ResponseEntity<?> responseEntity) {
            ColorLogger.green("-----------> RESPONSE : {}({}) = {} ({}ms)",
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),
                responseEntity.getBody(),
                executionTime);
        }
        // ResultResponse일 경우
        else if (result instanceof ResultResponse<?> resultResponse) {
            ColorLogger.green("-----------> RESPONSE : {}({}) = {} ({}ms)",
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),
                resultResponse.getData(),
                executionTime);
        }
        // 일반적인 Object 리턴 (int, String, List 등)
        else {
            ColorLogger.green("-----------> RESPONSE : {}({}) = {} ({}ms)",
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(),
                result,
                executionTime);
        }

        return result;  // 원래 응답 그대로 반환
    }


    private String getObjectDetails(Object arg) {
        StringBuilder details = new StringBuilder();
        // 파라미터의 클래스 Reflection 을 들고 와서 그 필드들을 하나 하나 까본다.
        Field[] fields = arg.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 식별 타입이 뭔지 확인한다. private final 상수 선언 시 확인에서 제외한다.
            // private final 멤버 변수는 보안상 오류가 나므로 생략한다.
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers)) {
                continue;
            }
            // private 멤버 변수도 접근 할 수 있도록 허용
            field.setAccessible(true);
            try {
                details.append((field.getName())).append("=");
                details.append((field.get(arg))).append(", ");
            } catch (IllegalAccessException e) {
                throw new GlobalException(ErrorCode.FAILED_TO_ACCESS_VARIABLE);
            }
        }

        if (details.length() > 2) {
            details.setLength(details.length() - 2);
        }

        return details.toString();
    }

    // Header 내용을 확인한다.
    private StringBuilder getHeaderDetail() {
        StringBuilder ans = new StringBuilder();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        ans.append(" 요청 주소: ").append(request.getRequestURL().toString())
            .append(" 요청Method: ").append(request.getMethod())
            .append(" IP 주소: ").append(request.getRemoteAddr());
        return ans;
    }
}
