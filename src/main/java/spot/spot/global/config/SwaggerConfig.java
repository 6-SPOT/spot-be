package spot.spot.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Map;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import spot.spot.global.util.ConstantUtil;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
            .group("all-api")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("SPOT API")
            .version("v1.0.1")
            .description(
                "<div style='text-align:center;'>"
                    + "<h1>HELLO WORLD!</h1>"
                    + "<h2>WELCOME TO SPOT API SERVER</h2>"
                    + "<hr/>"
                    + "<h2>규칙</h2>"
                    + "<p>(1) 숫자는 프론트팀이 사용하는 것.</p>"
                    + "<p>(2) 한글 indexing 된 것은 옛날 산물 혹은 Health Check.</p>"
                    + "<hr/>"
                    + "<h2>일 상태 전개도</h2>"
                    + "<div align='center'>"
                    + "<img src='https://soomin-bucket-1.s3.ap-northeast-2.amazonaws.com/static/Job_%EC%83%81%ED%83%9C_%EB%B3%80%EA%B2%BD_%EC%A0%84%EA%B0%9C%EB%8F%84.png' style='max-width:500px; border-radius:10px; box-shadow:2px 2px 10px rgba(0,0,0,0.1);'/>"
                    + "</div>"
                    + "</div>"
                    + "<style>"
                    + "h1, h2 { color: #2c3e50; font-family: Arial, sans-serif; }"
                    + "p { font-size: 14px; }"
                    + "</style>"
            )
            .contact(new Contact()
                .name("담당자 - 개발팀 고경훈")
                .email("rhrudgns159@gmail.com")
                .url("https://github.com/42kko")
            );

        SecurityScheme bearer = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat(ConstantUtil.AUTHORIZATION)
            .in(SecurityScheme.In.HEADER)
            .name(HttpHeaders.AUTHORIZATION);

        // Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList(ConstantUtil.AUTHORIZATION);

        Components components = new Components()
            .addSecuritySchemes(ConstantUtil.AUTHORIZATION, bearer);

        return new OpenAPI()
            .components(components)
            .addSecurityItem(addSecurityItem)
            .addServersItem(new Server().url("https://ilmatch.net")
                .description("Default Server URL"))
            .addServersItem(new Server().url("http://localhost:8080")
                .description("Local Development Server"))
            .addServersItem(new Server().url("http://171.16.24.158:8080")
                .description("Local Development Server"))
            .info(info)
            .components(components)
            .addSecurityItem(addSecurityItem);
    }
}
