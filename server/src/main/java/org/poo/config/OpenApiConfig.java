package org.poo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI alugaFacilOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlugaFácil API")
                        .description("API REST do sistema de aluguel de veículos AlugaFácil. "
                                + "Autentique-se em POST /auth/login e use o token retornado no botão Authorize.")
                        .version("1.0")
                        .contact(new Contact().name("Equipe AlugaFácil")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER))
                .components(new Components().addSecuritySchemes(ESQUEMA_BEARER,
                        new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description("Token (UUID) retornado por POST /auth/login.")));
    }
}
