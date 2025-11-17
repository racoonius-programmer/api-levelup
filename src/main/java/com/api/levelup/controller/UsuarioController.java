package com.api.levelup.controller;

import com.api.levelup.model.Usuario;
import com.api.levelup.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService UsuarioService;

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario Usuario) {
        return UsuarioService.guardarUsuario(Usuario);
    }

    @GetMapping
    public List<Usuario> obtenerTodas() {
        return UsuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return UsuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario Usuario) {
        try {
            Usuario actualizada = UsuarioService.actualizarUsuario(id, Usuario);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        UsuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}