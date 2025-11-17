package com.api.levelup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.levelup.model.Usuario;
import com.api.levelup.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testObtenerTodas() throws Exception {
        Usuario u1 = new Usuario(1L, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                               "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                               "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        Usuario u2 = new Usuario(2L, "juanito", "juanito@duoc.cl", "juanito123", "2000-05-12", 
                               "987654321", "Calle Secundaria 45", "Valparaíso", 
                               "Viña del Mar", "usuario", true, "/img/header/user-logo-generic-white-alt.png");

        Mockito.when(usuarioService.listarUsuarios()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].correo").value("juanito@duoc.cl"));
    }

    @Test
    void testCrearUsuario() throws Exception {
        Usuario nuevo = new Usuario(null, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                                  "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                                  "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        Usuario guardado = new Usuario(1L, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                                     "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                                     "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");

        Mockito.when(usuarioService.guardarUsuario(any(Usuario.class)))
                .thenReturn(guardado);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        Usuario u = new Usuario(1L, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                              "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                              "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");
        Mockito.when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(u));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        Mockito.when(usuarioService.obtenerUsuarioPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarUsuario() throws Exception {
        Usuario actualizado = new Usuario(3L, "maria", "maria@gmail.com", "maria123", "1995-09-20", 
                                        "555555555", "Av. Las Flores 12", "Región del Biobío", 
                                        "Concepción", "usuario", false, "/img/header/user-logo-generic-white-alt.png");
        Mockito.when(usuarioService.actualizarUsuario(eq(3L), any(Usuario.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/usuarios/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maria"))
                .andExpect(jsonPath("$.region").value("Región del Biobío"));
    }

    @Test
    void testEliminarUsuario() throws Exception {
        Mockito.doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}