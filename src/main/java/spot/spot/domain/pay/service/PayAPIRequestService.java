package spot.spot.domain.pay.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;

import java.util.Map;

@Component
@Slf4j
public class PayAPIRequestService {

    private final RestTemplate restTemplate = new RestTemplate();

    public <T> T payAPIRequest(String url, HttpEntity<Map<String, String>> requestEntity, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    "https://open-api.kakaopay.com/online/v1/payment/" + url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
            return response.getBody(); // ✅ 응답 객체 반환
        } catch (Exception e) {
            log.error("카카오페이 API 요청 실패: {}", url, e);
            throw new GlobalException(ErrorCode.FAIL_PAY_READY);
        }
    }

    public <T> T payfakeAPIRequest(String url, HttpEntity<Map<String, String>> requestEntity, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    ///환경 변수 넣어주세요.. (domain/fake-api/pay/ + url)입니다.
                    "http://172.16.24.136/fake-api/pay/" + url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
            return response.getBody(); // ✅ 응답 객체 반환
        } catch (Exception e) {
            log.error("카카오페이 API 요청 실패: {}", url, e);
            throw new GlobalException(ErrorCode.FAIL_PAY_READY);
        }
    }


}
