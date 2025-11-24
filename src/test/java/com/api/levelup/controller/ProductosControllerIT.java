package com.api.levelup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.api.levelup.model.Productos;
import com.api.levelup.model.Pedido;
import com.api.levelup.model.PedidoProducto;
import com.api.levelup.repository.ProductosRepository;
import com.api.levelup.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para ProductosController.
 * Valida específicamente la lógica de eliminación de productos con reglas de negocio.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductosControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductosRepository productosRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarBaseDeDatos() {
        pedidoRepository.deleteAll();
        productosRepository.deleteAll();
    }

    @Test
    void testEliminarProducto_Exitoso_CuandoNoEstaEnPedidos() throws Exception {
        // Given: Crear producto sin pedidos asociados
        Productos producto = new Productos("TEST001", "Producto Test", "/img/test.jpg", 
                                          15000, "Test Corp", "Test Dist", "Test Brand", 
                                          "Plástico", "Producto de prueba", "test.html", "test");
        productosRepository.save(producto);

        // When & Then: Eliminar producto debe ser exitoso
        mockMvc.perform(delete("/api/productos/TEST001"))
                .andExpect(status().isNoContent());

        // Verificar que el producto ya no existe
        mockMvc.perform(get("/api/productos/TEST001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarProducto_Falla_CuandoEstaEnPedidoActivo() throws Exception {
        // Given: Crear producto
        Productos producto = new Productos("FG001", "Frodo", "/img/frodo.png", 29990, 
                                          "LOTR Toys", "LOTR Dist", "LOTR", "Plástico", 
                                          "Figura de Frodo", "frodo.html", "figuras");
        productosRepository.save(producto);

        // Crear pedido activo con el producto
        Pedido pedido = new Pedido();
        pedido.setFecha("2025-11-24T12:00:00Z");
        pedido.setClienteId(1);
        pedido.setEstado("en preparacion"); // Estado activo
        pedido.setTotal(29990.0);
        
        // Agregar producto al pedido
        PedidoProducto pp = new PedidoProducto("FG001", "Frodo", 1, 29990.0);
        pedido.setProductos(Arrays.asList(pp));
        pedido.setProductosJson("[{\"codigo\":\"FG001\",\"nombre\":\"Frodo\",\"cantidad\":1,\"precio\":29990.0}]");
        
        pedidoRepository.save(pedido);

        // When & Then: Eliminar producto debe fallar
        mockMvc.perform(delete("/api/productos/FG001"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("ELIMINACIÓN BLOQUEADA")))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("pedidos activos")));

        // Verificar que el producto todavía existe
        mockMvc.perform(get("/api/productos/FG001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("FG001"));
    }

    @Test
    void testEliminarProducto_Exitoso_CuandoPedidoEstaCancelado() throws Exception {
        // Given: Crear producto
        Productos producto = new Productos("JM001", "Catan", "/img/catan.jpg", 29990, 
                                          "Catan Studio", "Catan Dist", "Catan", "Cartón", 
                                          "Juego Catan", "catan.html", "juegos_de_mesa");
        productosRepository.save(producto);

        // Crear pedido CANCELADO con el producto
        Pedido pedido = new Pedido();
        pedido.setFecha("2025-11-20T12:00:00Z");
        pedido.setClienteId(2);
        pedido.setEstado("CANCELADO"); // Estado inactivo
        pedido.setTotal(29990.0);
        
        PedidoProducto pp = new PedidoProducto("JM001", "Catan", 1, 29990.0);
        pedido.setProductos(Arrays.asList(pp));
        pedido.setProductosJson("[{\"codigo\":\"JM001\",\"nombre\":\"Catan\",\"cantidad\":1,\"precio\":29990.0}]");
        
        pedidoRepository.save(pedido);

        // When & Then: Eliminar producto debe ser exitoso porque el pedido está cancelado
        mockMvc.perform(delete("/api/productos/JM001"))
                .andExpect(status().isNoContent());

        // Verificar que el producto ya no existe
        mockMvc.perform(get("/api/productos/JM001"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPuedeEliminarProducto_RetornaTrue_SinPedidosActivos() throws Exception {
        // Given: Producto sin pedidos
        Productos producto = new Productos("AC001", "Control Xbox", "/img/xbox.jpg", 59990, 
                                          "Xbox", "Xbox Chile", "Xbox", "Plástico", 
                                          "Control inalámbrico", "xbox.html", "accesorios");
        productosRepository.save(producto);

        // When & Then: Debe poder eliminarse
        mockMvc.perform(get("/api/productos/AC001/puede-eliminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeEliminar").value(true));
    }

    @Test
    void testPuedeEliminarProducto_RetornaFalse_ConPedidosActivos() throws Exception {
        // Given: Producto con pedido activo
        Productos producto = new Productos("PP001", "Polera Level-Up", "/img/polera.png", 14990, 
                                          "Level-Up Gamer", "Level-Up Gamer", "Level-Up Gamer", 
                                          "Algodón", "Polera personalizada", "polera.html", "poleras_personalizadas");
        productosRepository.save(producto);

        // Crear pedido activo
        Pedido pedido = new Pedido();
        pedido.setFecha("2025-11-24T10:00:00Z");
        pedido.setClienteId(3);
        pedido.setEstado("en camino"); // Estado activo
        pedido.setTotal(14990.0);
        
        PedidoProducto pp = new PedidoProducto("PP001", "Polera Level-Up", 1, 14990.0);
        pedido.setProductos(Arrays.asList(pp));
        pedido.setProductosJson("[{\"codigo\":\"PP001\",\"nombre\":\"Polera Level-Up\",\"cantidad\":1,\"precio\":14990.0}]");
        
        pedidoRepository.save(pedido);

        // When & Then: No debe poder eliminarse
        mockMvc.perform(get("/api/productos/PP001/puede-eliminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeEliminar").value(false));
    }

    @Test
    void testCrearYObtenerProducto() throws Exception {
        // Given: Nuevo producto
        Productos producto = new Productos("MS001", "Mouse Logitech", "/img/mouse.jpg", 49990, 
                                          "Logitech", "Logitech Chile", "Logitech", "Plástico", 
                                          "Mouse gamer", "mouse.html", "mouse");

        // When: Crear producto
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigo").value("MS001"))
            .andExpect(jsonPath("$.nombre").value("Mouse Logitech"));

        // Then: Obtener producto
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codigo").value("MS001"))
            .andExpect(jsonPath("$[0].precio").value(49990));
    }
}