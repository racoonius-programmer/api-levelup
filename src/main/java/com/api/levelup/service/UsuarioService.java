package com.api.levelup.service;

import com.api.levelup.model.Usuario;
import com.api.levelup.repository.UsuarioRepository;
import com.api.levelup.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository UsuarioRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;

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

    /**
     * Elimina un usuario del sistema.
     * REGLA DE NEGOCIO: No se puede eliminar un usuario si tiene pedidos activos.
     * Un pedido se considera activo si su estado NO es "CANCELADO" o "ENTREGADO".
     * 
     * @param id ID del usuario a eliminar
     * @throws RuntimeException si el usuario tiene pedidos activos
     */
    public void eliminarUsuario(Long id) {
        // Verificar si el usuario existe
        if (!UsuarioRepository.existsById(id)) {
            throw new RuntimeException("No existe el Usuario con ID: " + id);
        }
        
        // Verificar si el usuario tiene pedidos activos
        // Convertimos Long a Integer para compatibilidad con PedidoRepository
        Integer usuarioIdInt = id.intValue();
        boolean tienePedidosActivos = pedidoRepository.existePedidoActivoPorClienteId(usuarioIdInt);
        
        if (tienePedidosActivos) {
            throw new RuntimeException(
                "No se puede eliminar el usuario porque tiene pedidos activos. " +
                "Para eliminar este usuario, primero debe cancelar o completar todos sus pedidos activos."
            );
        }
        
        // Si no tiene pedidos activos, proceder con la eliminación
        UsuarioRepository.deleteById(id);
    }
    
    /**
     * Verifica si un usuario puede ser eliminado.
     * Un usuario puede ser eliminado si no tiene pedidos activos.
     * 
     * @param id ID del usuario a verificar
     * @return true si el usuario puede ser eliminado, false en caso contrario
     */
    public boolean puedeEliminarUsuario(Long id) {
        if (!UsuarioRepository.existsById(id)) {
            return false;
        }
        Integer usuarioIdInt = id.intValue();
        return !pedidoRepository.existePedidoActivoPorClienteId(usuarioIdInt);
    }
}