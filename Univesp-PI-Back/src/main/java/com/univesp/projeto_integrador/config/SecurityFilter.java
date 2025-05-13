package com.univesp.projeto_integrador.config;

import com.univesp.projeto_integrador.model.Usuario;
import com.univesp.projeto_integrador.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de segurança para processar tokens JWT em cada requisição
 * Valida tokens e configura contexto de segurança
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = recoverToken(request);
        if (token != null) {
            String email = tokenService.validateToken(token);
            if (email != null) {
                // Verifica apenas se o usuário existe, sem carregar roles/permissões
                userRepository.findByEmail(email).ifPresent(user -> {
                    // Cria autenticação SEM autoridades (roles)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "").trim();
    }
}