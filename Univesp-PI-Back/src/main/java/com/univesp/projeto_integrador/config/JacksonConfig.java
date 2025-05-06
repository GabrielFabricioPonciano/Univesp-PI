package com.univesp.projeto_integrador.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações personalizadas para o Jackson (serialização JSON)
 * - Suporte para tipos de data do Java 8+
 * - Formatação adequada de datas
 */
@Configuration
public class JacksonConfig {

    /**
     * Configura o ObjectMapper para serialização JSON
     * @return ObjectMapper configurado
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Habilita suporte para LocalDate, LocalDateTime, etc.
        mapper.registerModule(new JavaTimeModule());

        // Evita serialização de datas como timestamp
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}