package spot.spot.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {
    //RestTemplate 이란 : 스프링에서 제공하는 http클라이언트 . http요청을 보내고 응답 받을때 활용
    //    String url = "https://jsonplaceholder.typicode.com/posts/1";
    //    RestTemplate restTemplate = new RestTemplate();
    //
    //    String response = restTemplate.getForObject(url, String.class);
    //      .getForObject(url,반환타입.class) 응답을 객체로 변환
    //      .getForEntity(url,ResponseEntity.class) http 응답전체(state,headers포함)반환
    //    System.out.println(response);
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
