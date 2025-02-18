package com.montreal.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Habilita CORS para todas as rotas
                .allowedOrigins(
                        "https://gestao-garantias-backend.dev.k8s.montreal.com.br", // Ambiente dev
                        "https://homol-msiav-mg.montreal.com.br", // Ambiente homologação
                        "https://msiav-mg-api.montreal.com.br" // Ambiente produção
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permitir todos os headers
                .exposedHeaders("Authorization", "Content-Disposition") // Headers expostos, se necessários
                .allowCredentials(true) // Permitir credenciais (cookies/autenticação)
                .maxAge(3600); // Tempo de cache para as configurações de CORS
    }
}
