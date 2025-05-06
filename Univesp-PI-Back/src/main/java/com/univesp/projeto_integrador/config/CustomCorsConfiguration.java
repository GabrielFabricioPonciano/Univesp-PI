package com.univesp.projeto_integrador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configurações personalizadas de CORS para permitir comunicação entre frontend e backend
 * Garante acesso seguro de origens específicas e métodos HTTP
 */
@Configuration
public class CustomCorsConfiguration {

    /**
     * Cria e configura o filtro CORS
     * @return Filtro CORS com políticas definidas
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configurações de segurança
        config.setAllowCredentials(true);                   // Permite cookies e autenticação
        config.addAllowedOrigin("http://localhost:4200");   // Origem permitida (frontend)
        config.addAllowedHeader("*");                       // Todos os headers permitidos
        config.addAllowedMethod("*");                       // Todos os métodos HTTP permitidos

        source.registerCorsConfiguration("/**", config);    // Aplica a todas as rotas
        return new CorsFilter(source);
    }
}