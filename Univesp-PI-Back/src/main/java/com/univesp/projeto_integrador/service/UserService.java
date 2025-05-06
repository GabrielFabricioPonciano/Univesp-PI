package com.univesp.projeto_integrador.service;

import com.univesp.projeto_integrador.model.Usuario;
import com.univesp.projeto_integrador.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//Refazer depois

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Usuario> findAll() {
        return userRepository.findAll();
    }

}
