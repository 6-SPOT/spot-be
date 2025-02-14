package spot.spot.global.config;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestClientTest
@Import(FeignConfigTest.TestFeignConfig.class)
public class FeignConfigTest {

    @FeignClient(name = "testFeignClient", url = "https://jsonplaceholder.typicode.com")
    public interface TestFeignClient {
        @GetMapping("/posts/{id}")
        FakeDTO getPost(@PathVariable("id") Long id);
    }

    @TestConfiguration
    public static class TestFeignConfig {
        @Bean
        public TestFeignClient testFeignClient() {
            return id -> new FakeDTO(id, "Mock Title", "Mock Body");
        }
    }

    @Test
    void testFeignClient() {
        TestFeignClient testFeignClient = new TestFeignConfig().testFeignClient();

        // Feign Client 호출
        FakeDTO response = testFeignClient.getPost(1L);

        // 응답 값 검증
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Mock Title");
    }

    @AllArgsConstructor
    @Getter
    public static class FakeDTO {
        long id;
        String title;
        String body;
    }
}
