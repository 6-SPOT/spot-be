package spot.spot.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import spot.spot.global.dto.response.ResultResponse;
import spot.spot.global.exception.ErrorCode;
import spot.spot.global.exception.GlobalException;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    // 해당 Advice 적용 범위
    @Override
    public boolean supports(MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    // 응답 변환 매서드
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {

        // 만약 반환 타입이 void이면 data 없이 응답
        if (Void.TYPE.equals(returnType.getParameterType())) {
            return new ResultResponse("success", "처리가 완료되었습니다.", null);
        }

        // 만약 String이면 예외 발생
        if (body instanceof String) {
            throw new GlobalException(ErrorCode.NOT_ALLOW_STRING);
        }

        if(body instanceof ResultResponse){
            return body;
        }
        return ResultResponse.success(body);
    }
}
