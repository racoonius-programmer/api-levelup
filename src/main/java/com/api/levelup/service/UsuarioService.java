package com.api.levelup.service;

import com.api.levelup.model.Usuario;
import com.api.levelup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository UsuarioRepository;

    public Usuario guardarUsuario(Usuario Usuario) {
        return UsuarioRepository.save(Usuario);
    }

    public List<Usuario> listarUsuarios() {
        return UsuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return UsuarioRepository.findById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Usuario existente = UsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el Usuario"));
        existente.setUsername(usuario.getUsername());
        existente.setCorreo(usuario.getCorreo());
        existente.setContrasena(usuario.getContrasena());
        existente.setFechaNacimiento(usuario.getFechaNacimiento());
        existente.setTelefono(usuario.getTelefono());
        existente.setDireccion(usuario.getDireccion());
        existente.setRegion(usuario.getRegion());
        existente.setComuna(usuario.getComuna());
        existente.setRol(usuario.getRol());
        existente.setDescuentoDuoc(usuario.getDescuentoDuoc());
        existente.setFotoPerfil(usuario.getFotoPerfil());
        return UsuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        UsuarioRepository.deleteById(id);
    }
}