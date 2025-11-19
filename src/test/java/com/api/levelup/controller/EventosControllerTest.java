package com.api.levelup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.levelup.model.Eventos;
import com.api.levelup.service.EventosService;
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

@WebMvcTest(EventosController.class)
class EventosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventosService eventosService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testObtenerTodos() throws Exception {
        Eventos e1 = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                               "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        Eventos e2 = new Eventos(2, "/img/eventos/teatro.jpg", "Obra Teatro", 
                               "Romeo y Julieta", "Clásica obra de Shakespeare");

        Mockito.when(eventosService.listarEventos()).thenReturn(Arrays.asList(e1, e2));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Concierto de Rock 2024"))
                .andExpect(jsonPath("$[1].imagenAlt").value("Obra Teatro"));
    }

    @Test
    void testCrearEvento() throws Exception {
        Eventos nuevo = new Eventos(null, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                  "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        Eventos guardado = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                     "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");

        Mockito.when(eventosService.guardarEvento(any(Eventos.class)))
                .thenReturn(guardado);

        mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Concierto de Rock 2024"));
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        Eventos e = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                              "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        Mockito.when(eventosService.obtenerEventoPorId(1L)).thenReturn(Optional.of(e));

        mockMvc.perform(get("/api/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Concierto de Rock 2024"));
    }

    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        Mockito.when(eventosService.obtenerEventoPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/eventos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarEvento() throws Exception {
        Eventos actualizado = new Eventos(1, "/img/eventos/deporte.jpg", "Partido Fútbol", 
                                        "Final Copa América", "Final emocionante de la Copa América");
        Mockito.when(eventosService.actualizarEvento(eq(1L), any(Eventos.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/eventos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Final Copa América"))
                .andExpect(jsonPath("$.imagenAlt").value("Partido Fútbol"));
    }

    @Test
    void testEliminarEvento() throws Exception {
        Mockito.doNothing().when(eventosService).eliminarEvento(1L);

        mockMvc.perform(delete("/api/eventos/1"))
                .andExpect(status().isNoContent());
    }
}