package spot.spot.global.response.handler;

import javax.annotation.Nonnull;
import javax.xml.transform.Result;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import spot.spot.global.response.format.ErrorCode;
import spot.spot.global.response.format.ResultResponse;
import spot.spot.global.response.format.GlobalException;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    // 해당 Advice 적용 범위
    @Override
    public boolean supports(@Nonnull  MethodParameter returnType,
        @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    // 응답 변환 매서드
    @NotNull
    @Override
    public Object beforeBodyWrite(
        @Nonnull Object body,
        @Nonnull MethodParameter returnType,
        @Nonnull MediaType selectedContentType,
        @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request,
        @Nonnull ServerHttpResponse response) {
        //  Swagger docs page를 띄우기 위한 요청이거나 byte 요청은 변환 없이 값 그대로 반환
        String requestPath = request.getURI().getPath();
        if (requestPath.startsWith("/v3/api-docs") || requestPath.startsWith("/swagger-ui") || body instanceof byte[]) {
            return body;
        }
        // 만약 반환 타입이 void이면 data 없이 응답
        if (Void.TYPE.equals(returnType.getParameterType())) {
            return ResultResponse.success("요청 완료");
        }
        // 만약 String이면 예외 발생
        if (body instanceof String) {throw new GlobalException(ErrorCode.NOT_ALLOW_STRING);}
        if(body instanceof ResultResponse<?>) return body;
        return ResultResponse.success(body);
    }
}
