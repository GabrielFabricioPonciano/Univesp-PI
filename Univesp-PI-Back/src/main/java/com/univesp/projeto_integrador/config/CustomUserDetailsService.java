package com.univesp.projeto_integrador.config;

import com.univesp.projeto_integrador.model.Usuario;
import com.univesp.projeto_integrador.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Serviço personalizado para carregar detalhes do usuário durante a autenticação
 * Implementa a interface UserDetailsService do Spring Security
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    /**
     * Carrega usuário pelo email (username)
     * @param username Email do usuário (atua como username)
     * @return UserDetails com informações do usuário
     * @throws UsernameNotFoundException Se usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList() // Lista de autoridades/permissões (vazia por padrão)
        );
    }
}