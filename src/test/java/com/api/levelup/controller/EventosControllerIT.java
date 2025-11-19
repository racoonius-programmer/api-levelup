package com.api.levelup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.levelup.model.Eventos;
import com.api.levelup.repository.EventosRepository;
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
class EventosControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventosRepository eventosRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        eventosRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerEvento() throws Exception {
        Eventos evento = new Eventos(null, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                   "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");

        mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evento)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Concierto de Rock 2024"))
            .andExpect(jsonPath("$.imagenAlt").value("Concierto Rock"));

        mockMvc.perform(get("/api/eventos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].titulo").value("Concierto de Rock 2024"))
            .andExpect(jsonPath("$[0].descripcion").value("Gran concierto de rock con las mejores bandas"));
    }

    @Test
    void testActualizarEvento() throws Exception {
        Eventos evento = new Eventos(null, "/img/eventos/teatro.jpg", "Obra Teatro", 
                                   "Romeo y Julieta", "Clásica obra de Shakespeare");

        // Crear evento
        String response = mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evento)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Eventos eventoCreado = objectMapper.readValue(response, Eventos.class);

        // Actualizar evento
        eventoCreado.setImagenSrc("/img/eventos/deporte.jpg");
        eventoCreado.setImagenAlt("Partido Fútbol");
        eventoCreado.setTitulo("Final Copa América");
        eventoCreado.setDescripcion("Final emocionante de la Copa América");

        mockMvc.perform(put("/api/eventos/" + eventoCreado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventoCreado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Final Copa América"))
            .andExpect(jsonPath("$.imagenAlt").value("Partido Fútbol"));
    }

    @Test
    void testEliminarEvento() throws Exception {
        Eventos evento = new Eventos(null, "/img/eventos/musica.jpg", "Festival Música", 
                                   "Festival de Verano 2024", "El mejor festival de música del año");

        // Crear evento
        String response = mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evento)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Eventos eventoCreado = objectMapper.readValue(response, Eventos.class);

        // Eliminar evento
        mockMvc.perform(delete("/api/eventos/" + eventoCreado.getId()))
            .andExpect(status().isNoContent());

        // Verificar que no existe
        mockMvc.perform(get("/api/eventos/" + eventoCreado.getId()))
            .andExpect(status().isNotFound());
    }
}