package com.api.levelup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.levelup.model.Usuario;
import com.api.levelup.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "admin", "admin@gmail.com", "admin123", "1990-01-01", 
                                    "123456789", "Av. Principal 100", "Región Metropolitana de Santiago", 
                                    "Santiago", "admin", false, "/img/header/user-logo-generic-white-alt.png");

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.correo").value("admin@gmail.com"));

        mockMvc.perform(get("/api/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("admin"))
            .andExpect(jsonPath("$[0].region").value("Región Metropolitana de Santiago"));
    }

    @Test
    void testActualizarUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "juanito", "juanito@duoc.cl", "juanito123", "2000-05-12", 
                                    "987654321", "Calle Secundaria 45", "Valparaíso", 
                                    "Viña del Mar", "usuario", true, "/img/header/user-logo-generic-white-alt.png");

        // Crear usuario
        String response = mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Usuario usuarioCreado = objectMapper.readValue(response, Usuario.class);

        // Actualizar usuario
        usuarioCreado.setTelefono("999888777");
        usuarioCreado.setRegion("Región del Biobío");
        usuarioCreado.setComuna("Concepción");

        mockMvc.perform(put("/api/usuarios/" + usuarioCreado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioCreado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.telefono").value("999888777"))
            .andExpect(jsonPath("$.region").value("Región del Biobío"));
    }

    @Test
    void testEliminarUsuario() throws Exception {
        Usuario usuario = new Usuario(null, "maria", "maria@gmail.com", "maria123", "1995-09-20", 
                                    "555555555", "Av. Las Flores 12", "Región del Biobío", 
                                    "Concepción", "usuario", false, "/img/header/user-logo-generic-white-alt.png");

        // Crear usuario
        String response = mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Usuario usuarioCreado = objectMapper.readValue(response, Usuario.class);

        // Eliminar usuario
        mockMvc.perform(delete("/api/usuarios/" + usuarioCreado.getId()))
            .andExpect(status().isNoContent());

        // Verificar que no existe
        mockMvc.perform(get("/api/usuarios/" + usuarioCreado.getId()))
            .andExpect(status().isNotFound());
    }
}