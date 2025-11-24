package com.api.levelup.controller;

import com.api.levelup.model.Usuario;
import com.api.levelup.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * Elimina un usuario. Implementa validación de regla de negocio.
     * No permite eliminar usuarios con pedidos activos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            UsuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verifica si un usuario puede ser eliminado.
     * Útil para mostrar/ocultar botones de eliminación en el frontend.
     */
    @GetMapping("/{id}/puede-eliminar")
    public ResponseEntity<Map<String, Boolean>> puedeEliminar(@PathVariable Long id) {
        boolean puedeEliminar = UsuarioService.puedeEliminarUsuario(id);
        return ResponseEntity.ok(Map.of("puedeEliminar", puedeEliminar));
    }
}