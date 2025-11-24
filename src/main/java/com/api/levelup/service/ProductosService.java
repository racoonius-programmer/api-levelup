package com.api.levelup.service;

import com.api.levelup.model.Productos;
import com.api.levelup.repository.ProductosRepository;
import com.api.levelup.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de productos.
 * Contiene la lógica de negocio para CRUD de productos y validaciones de eliminación.
 */
@Service
public class ProductosService {

    @Autowired
    private ProductosRepository productosRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Guarda un nuevo producto en la base de datos.
     */
    public Productos guardarProducto(Productos producto) {
        return productosRepository.save(producto);
    }

    /**
     * Obtiene todos los productos de la base de datos.
     */
    public List<Productos> listarProductos() {
        return productosRepository.findAll();
    }

    /**
     * Busca un producto por su código único.
     */
    public Optional<Productos> obtenerProductoPorCodigo(String codigo) {
        return productosRepository.findById(codigo);
    }

    /**
     * Actualiza un producto existente con nueva información.
     */
    public Productos actualizarProducto(String codigo, Productos producto) {
        return productosRepository.findById(codigo)
                .map(p -> {
                    p.setNombre(producto.getNombre());
                    p.setImagen(producto.getImagen());
                    p.setPrecio(producto.getPrecio());
                    p.setFabricante(producto.getFabricante());
                    p.setDistribuidor(producto.getDistribuidor());
                    p.setMarca(producto.getMarca());
                    p.setMaterial(producto.getMaterial());
                    p.setDescripcion(producto.getDescripcion());
                    p.setEnlace(producto.getEnlace());
                    p.setCategoria(producto.getCategoria());
                    return productosRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));
    }

    /**
     * Elimina un producto del sistema aplicando reglas de negocio.
     * 
     * REGLA DE NEGOCIO IMPLEMENTADA:
     * Un producto NO puede ser eliminado si está asociado a pedidos activos.
     * 
     * ESTADOS DE PEDIDO CONSIDERADOS ACTIVOS:
     * - "en preparacion" 
     * - "en camino"
     * - "pendiente"
     * - Cualquier otro estado excepto "CANCELADO" y "ENTREGADO"
     * 
     * PROCESO DE ELIMINACIÓN:
     * 1. Verifica que el producto existe
     * 2. Consulta si hay pedidos activos que contengan este producto
     * 3. Si hay pedidos activos, lanza excepción con mensaje descriptivo
     * 4. Si no hay pedidos activos, procede con la eliminación física
     * 
     * @param codigo código único del producto a eliminar
     * @throws RuntimeException si el producto no existe
     * @throws RuntimeException si el producto está en pedidos activos
     */
    public void eliminarProducto(String codigo) {
        // PASO 1: Verificar existencia del producto
        if (!productosRepository.existsById(codigo)) {
            throw new RuntimeException("No existe el Producto con código: " + codigo);
        }
        
        // PASO 2: Verificar regla de negocio - productos en pedidos activos
        boolean estaEnPedidosActivos = pedidoRepository.existePedidoActivoConProducto(codigo);
        
        if (estaEnPedidosActivos) {
            throw new RuntimeException(
                String.format(
                    "❌ ELIMINACIÓN BLOQUEADA: El producto '%s' no puede ser eliminado porque está " +
                    "asociado a uno o más pedidos activos. Para eliminar este producto, primero debe " +
                    "cancelar o marcar como 'ENTREGADO' todos los pedidos que lo contengan. " +
                    "Estados que bloquean la eliminación: 'en preparacion', 'en camino', 'pendiente', etc.",
                    codigo
                )
            );
        }
        
        // PASO 3: Eliminación física del producto
        System.out.println("✅ ELIMINACIÓN PERMITIDA: Producto '" + codigo + "' eliminado exitosamente");
        productosRepository.deleteById(codigo);
    }
    
    /**
     * Verifica si un producto puede ser eliminado sin ejecutar la eliminación.
     * Útil para habilitar/deshabilitar botones en el frontend.
     * 
     * @param codigo código del producto a verificar
     * @return true si puede ser eliminado (no está en pedidos activos), false en caso contrario
     */
    public boolean puedeEliminarProducto(String codigo) {
        if (!productosRepository.existsById(codigo)) {
            return false; // No existe el producto
        }
        // Retorna true solo si NO está en pedidos activos
        return !pedidoRepository.existePedidoActivoConProducto(codigo);
    }

    // Métodos de búsqueda adicionales
    public List<Productos> buscarPorCategoria(String categoria) {
        return productosRepository.findByCategoria(categoria);
    }

    public List<Productos> buscarPorFabricante(String fabricante) {
        return productosRepository.findByFabricante(fabricante);
    }

    public List<Productos> buscarPorMarca(String marca) {
        return productosRepository.findByMarca(marca);
    }

    public List<Productos> buscarPorRangoPrecio(Integer precioMin, Integer precioMax) {
        return productosRepository.findByPrecioBetween(precioMin, precioMax);
    }

    public List<Productos> buscarPorNombre(String nombre) {
        return productosRepository.findByNombreContainingIgnoreCase(nombre);
    }
}