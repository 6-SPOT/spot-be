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
            .title("SPOT")
            .version("v0.0.3")
            .description(
                "<br/> <h2 align='center'> 일 상태 전개도 </h2>"
                + "<div align='center' width = '500px'>"
                    + "<img src='https://soomin-bucket-1.s3.ap-northeast-2.amazonaws.com/static/Job_%EC%83%81%ED%83%9C_%EB%B3%80%EA%B2%BD_%EC%A0%84%EA%B0%9C%EB%8F%84.png'/>"
                    + "</div>")
                .contact(new Contact()
                .name("담당자 - 개발팀 고경훈")
                .email("rhrudgns159@gmail.com")
                .url("https://github.com/42kko"));

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
            .info(info)
            .components(components)
            .addSecurityItem(addSecurityItem);
    }
}
