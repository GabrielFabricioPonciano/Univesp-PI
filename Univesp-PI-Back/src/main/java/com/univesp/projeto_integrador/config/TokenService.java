package com.univesp.projeto_integrador.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.univesp.projeto_integrador.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera token JWT para um usuário
     * @param usuario Entidade do usuário
     * @return Token JWT assinado
     */
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("projeto_integrador")    // Emissor do token
                    .withSubject(usuario.getEmail())     // Identificação do usuário
                    .withExpiresAt(generateExpirationDate()) // Expiração
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro na geração do token", exception);
        }
    }

    /**
     * Valida e decodifica um token JWT
     * @param token Token a ser validado
     * @return Email do usuário (subject) ou null se inválido
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("projeto_integrador")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Gera data de expiração do token (2 horas)
     * @return Instant com data/hora de expiração
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00")); // Fuso horário de São Paulo
    }
}