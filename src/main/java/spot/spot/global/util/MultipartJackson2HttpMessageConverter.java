package spot.spot.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

/*
* Spring의 HttpMessageConverter는 들어온 요청의 Content-Type을 기준으로 이를 변환시킬 Converter를 고른다.
* multipart/form-data로 보내게되면 content-type이 form-data여서 MappingJackson2HttpMessageConverter
* 가 동작하지 않음. 따라서 json 요청은 Spring MVC 까지 들어오지 않게 된다.
*
* */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    protected MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        // form 데이터든, json이든 모든 형태의 바이너리 데이터를 처리하겠다. 라는 뜻
        // 즉 Multipart 내부의 json도 안가리고 받아서 처리하겠다.
        // 기존의 MappingJackson2HttpMessageConverter는 multipart/form-data 내부의 application/json을
        // 처리할 수 없다.
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    // 응답이 pplication/octet-stream(byte array)로 변환되는 것을 막기 위해 응답에서는 안 씀
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }

}
