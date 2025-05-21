package study.goorm.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    public SwaggerConfig(MappingJackson2HttpMessageConverter converter) {
        /**
         * Swagger UI를 통해서 MultipartForm을 통해 binary file과 json 타입의 DTO를 동시에 요청하는 경우
         * json 타입의 DTO에 해당하는 content-type이 null로 들어오는 문제가 있음 (Swagger 문서를 봐도 특별한 언급이 없음)
         * content-type이 null인 경우 spring에서는 application/octet-stream으로 인식하여 처리하게 됨
         * 하지만 application/octet-stream에 대한 기본 컨버터가 없기 때문에 MappingJackson2HttpMessageConverter에 application/octet-stream을 추가 함
         */
        List<MediaType> supportMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportMediaTypes);
    }

    @Bean
    public OpenAPI goormStudyAPI() {
        Info info = new Info()
                .title("GOORM API")
                .description("GOORM API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}