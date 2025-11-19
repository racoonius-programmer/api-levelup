package com.api.levelup.service;

import com.api.levelup.model.Eventos;
import com.api.levelup.repository.EventosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventosService {
    @Autowired
    private EventosRepository eventosRepository;

    public Eventos guardarEvento(Eventos evento) {
        return eventosRepository.save(evento);
    }

    public List<Eventos> listarEventos() {
        return eventosRepository.findAll();
    }

    public Optional<Eventos> obtenerEventoPorId(Long id) {
        return eventosRepository.findById(id);
    }

    public Eventos actualizarEvento(Long id, Eventos evento) {
        Eventos existente = eventosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el Evento"));
        existente.setImagenSrc(evento.getImagenSrc());
        existente.setImagenAlt(evento.getImagenAlt());
        existente.setTitulo(evento.getTitulo());
        existente.setDescripcion(evento.getDescripcion());
        return eventosRepository.save(existente);
    }

    public void eliminarEvento(Long id) {
        eventosRepository.deleteById(id);
    }
}