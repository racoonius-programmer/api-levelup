package com.api.levelup.controller;

import com.api.levelup.model.Productos;
import com.api.levelup.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @PostMapping
    public Productos crearProducto(@RequestBody Productos producto) {
        return productosService.guardarProducto(producto);
    }

    @GetMapping
    public List<Productos> obtenerTodos() {
        return productosService.listarProductos();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Productos> obtenerPorCodigo(@PathVariable String codigo) {
        return productosService.obtenerProductoPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Productos> actualizar(@PathVariable String codigo, @RequestBody Productos producto) {
        try {
            Productos actualizado = productosService.actualizarProducto(codigo, producto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un producto. Implementa validación de regla de negocio.
     * No permite eliminar productos que estén en pedidos activos.
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable String codigo) {
        try {
            productosService.eliminarProducto(codigo);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verifica si un producto puede ser eliminado.
     * Útil para mostrar/ocultar botones de eliminación en el frontend.
     */
    @GetMapping("/{codigo}/puede-eliminar")
    public ResponseEntity<Map<String, Boolean>> puedeEliminar(@PathVariable String codigo) {
        boolean puedeEliminar = productosService.puedeEliminarProducto(codigo);
        return ResponseEntity.ok(Map.of("puedeEliminar", puedeEliminar));
    }

    // Endpoints adicionales para búsquedas específicas
    @GetMapping("/categoria/{categoria}")
    public List<Productos> obtenerPorCategoria(@PathVariable String categoria) {
        return productosService.buscarPorCategoria(categoria);
    }

    @GetMapping("/fabricante/{fabricante}")
    public List<Productos> obtenerPorFabricante(@PathVariable String fabricante) {
        return productosService.buscarPorFabricante(fabricante);
    }

    @GetMapping("/marca/{marca}")
    public List<Productos> obtenerPorMarca(@PathVariable String marca) {
        return productosService.buscarPorMarca(marca);
    }

    @GetMapping("/precio")
    public List<Productos> obtenerPorRangoPrecio(@RequestParam Integer min, @RequestParam Integer max) {
        return productosService.buscarPorRangoPrecio(min, max);
    }

    @GetMapping("/buscar")
    public List<Productos> buscarPorNombre(@RequestParam String nombre) {
        return productosService.buscarPorNombre(nombre);
    }
}