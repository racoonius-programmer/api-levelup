package com.api.levelup.controller;
import com.api.levelup.model.Eventos;
import com.api.levelup.service.EventosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventosController {

    @Autowired
    private EventosService eventosService;

    @PostMapping
    public Eventos crearEvento(@RequestBody Eventos evento) {
        return eventosService.guardarEvento(evento);
    }

    @GetMapping
    public List<Eventos> obtenerTodos() {
        return eventosService.listarEventos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Eventos> obtenerPorId(@PathVariable Long id) {
        return eventosService.obtenerEventoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Eventos> actualizar(@PathVariable Long id, @RequestBody Eventos evento) {
        try {
            Eventos actualizado = eventosService.actualizarEvento(id, evento);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventosService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}