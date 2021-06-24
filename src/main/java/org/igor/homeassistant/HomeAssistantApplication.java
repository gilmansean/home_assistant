package org.igor.homeassistant;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class HomeAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeAssistantApplication.class, args);
    }

    /*
        Produce a Swagger UI along with automated spec.
     */
    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
                                 @Value("${application-version}") String appVersion) {
        return new OpenAPI().info(new Info().title("Igor Home Assistant API")
                                            .version(appVersion)
                                            .description(appDesciption)
                                            .termsOfService("http://swagger.io/terms/")
                                            .license(new License().name("Apache 2.0")
                                                                  .url("http://springdoc.org")))
                            .components(new Components().addSecuritySchemes("bearer-key", new SecurityScheme().type(
                                    SecurityScheme.Type.HTTP)
                                                                                                              .scheme("bearer")
                                                                                                              .bearerFormat(
                                                                                                                      "JWT")));
    }
}
