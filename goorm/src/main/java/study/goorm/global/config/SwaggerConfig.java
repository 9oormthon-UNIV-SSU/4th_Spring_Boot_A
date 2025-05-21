package study.goorm.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

//    // 오류 해결
//    @Autowired
//    private List<MappingJackson2HttpMessageConverter> converters;
//
//    // 오류 해결
//    @PostConstruct
//    public void customizeConverters() {
//        for (MappingJackson2HttpMessageConverter converter : converters) {
//            List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
//            mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//            converter.setSupportedMediaTypes(mediaTypes);
//        }
//    }
    // 오류 해결
    @Autowired
    public void configureMessageConverter(MappingJackson2HttpMessageConverter converter) {
        List<MediaType> supportMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
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