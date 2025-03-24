package spot.spot.domain.member.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.GlobalException;


import static org.springframework.web.util.UriComponentsBuilder.*;

@Component
@Slf4j
public class LoginFakeApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public <T> T loginfakeAPIRequest(String memberId, Class<T> responseType) {
        try {
            ///여기 url도 바꿔야합니다!! 환경 변수 설정 부탁드립니다..!
            String requestUrl = fromHttpUrl("http://172.16.24.136:8080/fake-api/login/token")
                    .queryParam("memberId", memberId)
                    .toUriString();

            ResponseEntity<T> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            return response.getBody(); // ✅ 응답 객체 반환
        } catch (Exception e) {
            log.error("fakeApi Server 연결 에러 : {}", e.getMessage(), e);
            throw new GlobalException(ErrorCode.FAIL_LOGIN);
        }
    }
}
