package com.api.levelup.service;

import com.api.levelup.model.Eventos;
import com.api.levelup.repository.EventosRepository;
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

class EventosServiceTest {

    @Mock
    private EventosRepository eventosRepository;

    @InjectMocks
    private EventosService eventosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarEvento() {
        Eventos evento = new Eventos(null, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                   "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        Eventos eventoGuardado = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                           "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        when(eventosRepository.save(evento)).thenReturn(eventoGuardado);

        Eventos resultado = eventosService.guardarEvento(evento);
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getTitulo()).isEqualTo("Concierto de Rock 2024");
        assertThat(resultado.getImagenSrc()).isEqualTo("/img/eventos/concierto.jpg");
        assertThat(resultado.getImagenAlt()).isEqualTo("Concierto Rock");
        verify(eventosRepository).save(evento);
    }

    @Test
    void testListarEventos() {
        Eventos e1 = new Eventos(1, "/img/eventos/teatro.jpg", "Obra Teatro", 
                               "Romeo y Julieta", "Clásica obra de Shakespeare");
        Eventos e2 = new Eventos(2, "/img/eventos/deporte.jpg", "Partido Fútbol", 
                               "Final Copa América", "Final emocionante de la Copa América");
        when(eventosRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Eventos> resultado = eventosService.listarEventos();
        assertThat(resultado).hasSize(2).contains(e1, e2);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Romeo y Julieta");
        assertThat(resultado.get(1).getTitulo()).isEqualTo("Final Copa América");
        verify(eventosRepository).findAll();
    }

    @Test
    void testObtenerEventoPorId() {
        Eventos e1 = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                               "Concierto de Rock 2024", "Gran concierto de rock con las mejores bandas");
        when(eventosRepository.findById(1L)).thenReturn(Optional.of(e1));

        Optional<Eventos> resultado = eventosService.obtenerEventoPorId(1L);
        assertThat(resultado).isPresent().contains(e1);
        assertThat(resultado.get().getTitulo()).isEqualTo("Concierto de Rock 2024");
        assertThat(resultado.get().getDescripcion()).isEqualTo("Gran concierto de rock con las mejores bandas");
        verify(eventosRepository).findById(1L);
    }

    @Test
    void testActualizarEvento() {
        Eventos eventoExistente = new Eventos(1, "/img/eventos/concierto.jpg", "Concierto Rock", 
                                            "Concierto de Rock 2024", "Gran concierto de rock");
        Eventos eventoActualizado = new Eventos(1, "/img/eventos/concierto_nuevo.jpg", "Concierto Pop", 
                                               "Concierto de Pop 2024", "Fantástico concierto de pop");
        
        when(eventosRepository.findById(1L)).thenReturn(Optional.of(eventoExistente));
        when(eventosRepository.save(any(Eventos.class))).thenReturn(eventoActualizado);

        Eventos resultado = eventosService.actualizarEvento(1L, eventoActualizado);
        assertThat(resultado.getTitulo()).isEqualTo("Concierto de Pop 2024");
        assertThat(resultado.getImagenAlt()).isEqualTo("Concierto Pop");
        verify(eventosRepository).findById(1L);
        verify(eventosRepository).save(any(Eventos.class));
    }

    @Test
    void testEliminarEvento() {
        doNothing().when(eventosRepository).deleteById(1L);
        eventosService.eliminarEvento(1L);
        verify(eventosRepository).deleteById(1L);
    }
}