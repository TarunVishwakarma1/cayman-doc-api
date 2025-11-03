package com.newgen.cig.cayman.document.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cayman Document API")
                        .version("1.0")
                        .description("Document API for Cayman Island to retrieve documents")
                        .contact(new Contact()
                                .name("Tarun Vishwakarma")
                                .email("tarun.vishwakarma@newgensoft.com")
                                .url("https://www.newgensoft.com")
                        )
                        .termsOfService("https://newgensoft.com/terms-of-use/")
                );
    }
}
