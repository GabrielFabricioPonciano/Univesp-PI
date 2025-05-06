package com.univesp.projeto_integrador.controller;

import com.univesp.projeto_integrador.config.TokenService;
import com.univesp.projeto_integrador.dto.LoginRequestDTO;
import com.univesp.projeto_integrador.dto.RegisterRequestDTO;
import com.univesp.projeto_integrador.dto.ResponseDTO;
import com.univesp.projeto_integrador.model.Usuario;
import com.univesp.projeto_integrador.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
//Refazer depois

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Tratamento do login
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
        Optional<Usuario> userOptional = repository.findByEmail(body.email()); // Corrigido para Optional<Usuario>

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("Usuário não encontrado com o email " + body.email(), null));
        }

        Usuario usuario = userOptional.get(); // Removido o cast desnecessário

        if (!passwordEncoder.matches(body.password(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO("Senha incorreta", null));
        }

        String token = tokenService.generateToken(usuario);
        return ResponseEntity.ok(new ResponseDTO(usuario.getName(), token));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body) {
        Optional<Usuario> existingUser = repository.findByEmail(body.email()); // Corrigido para Optional<Usuario>

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("Email já cadastrado", null));
        }

        Usuario newUsuario = new Usuario();
        newUsuario.setPassword(passwordEncoder.encode(body.password()));
        newUsuario.setEmail(body.email());
        newUsuario.setName(body.name());

        repository.save(newUsuario);

        String token = tokenService.generateToken(newUsuario);
        return ResponseEntity.ok(new ResponseDTO(newUsuario.getName(), token));
    }

    // Tratamento global de exceções (opcional)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Ocorreu um erro inesperado: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
