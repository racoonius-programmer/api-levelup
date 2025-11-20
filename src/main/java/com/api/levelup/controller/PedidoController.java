package com.api.levelup.controller;

import com.api.levelup.model.Pedido;
import com.api.levelup.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para administrar pedidos.
 * Endpoints:
 * - POST /api/pedidos : crear pedido
 * - GET /api/pedidos : listar pedidos
 * - GET /api/pedidos/{id} : obtener por id
 * - PUT /api/pedidos/{id} : actualizar pedido completo
 * - PATCH /api/pedidos/{id}/estado : actualizar sólo el estado
 * - DELETE /api/pedidos/{id} : eliminar
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return pedidoService.guardarPedido(pedido);
    }

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoService.listarPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Integer id) {
        return pedidoService.obtenerPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        try {
            Pedido actualizado = pedidoService.actualizarPedido(id, pedido);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Integer id, @RequestBody String estado) {
        try {
            Pedido actualizado = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
