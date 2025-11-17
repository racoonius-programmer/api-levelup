package com.api.levelup.service;

import com.api.levelup.model.Usuario;
import com.api.levelup.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarUsuario() {
        Usuario usuario = new Usuario(null, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                                    "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                                    "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        Usuario usuarioGuardado = new Usuario(1L, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                                            "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                                            "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.guardarUsuario(usuario);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo("admin");
        assertThat(resultado.getCorreo()).isEqualTo("admin@gmail.com");
        assertThat(resultado.getRol()).isEqualTo("admin");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testListarUsuarios() {
        Usuario u1 = new Usuario(2L, "juanito", "juanito@duoc.cl", "juanito123", "2000-05-12", 
                               "987654321", "Calle Secundaria 45", "Valparaíso", 
                               "Viña del Mar", "usuario", true, "/img/header/user-logo-generic-white-alt.png");
        Usuario u2 = new Usuario(3L, "maria", "maria@gmail.com", "maria123", "1995-09-20", 
                               "555555555", "Av. Las Flores 12", "Región del Biobío", 
                               "Concepción", "usuario", false, "/img/header/user-logo-generic-white-alt.png");
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> resultado = usuarioService.listarUsuarios();
        assertThat(resultado).hasSize(2).contains(u1, u2);
        assertThat(resultado.get(0).getUsername()).isEqualTo("juanito");
        assertThat(resultado.get(0).getDescuentoDuoc()).isTrue();
        assertThat(resultado.get(1).getUsername()).isEqualTo("maria");
        assertThat(resultado.get(1).getDescuentoDuoc()).isFalse();
        verify(usuarioRepository).findAll();
    }

    @Test
    void testObtenerUsuarioPorId() {
        Usuario u1 = new Usuario(1L, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                               "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                               "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u1));

        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);
        assertThat(resultado).isPresent().contains(u1);
        assertThat(resultado.get().getUsername()).isEqualTo("admin");
        assertThat(resultado.get().getRol()).isEqualTo("admin");
        assertThat(resultado.get().getRegion()).isEqualTo("Región Metropolitana de Santiago");
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testEliminarUsuario() {
        doNothing().when(usuarioRepository).deleteById(1L);
        usuarioService.eliminarUsuario(1L);
        verify(usuarioRepository).deleteById(1L);
    }
}