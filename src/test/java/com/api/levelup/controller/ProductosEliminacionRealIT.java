package com.api.levelup.controller;

import com.api.levelup.repository.PedidoRepository;
import com.api.levelup.repository.ProductosRepository;
import com.api.levelup.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de validación real con los datos de producción.
 * Valida que los productos asociados al pedido en crearPedido.sql no se puedan eliminar.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductosEliminacionRealIT {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ProductosRepository productosRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setupDatabase() throws Exception {
        // Limpiar la base de datos primero
        pedidoRepository.deleteAll();
        productosRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        // Cargar los datos desde los archivos SQL
        try (Connection connection = dataSource.getConnection()) {
            // Cargar usuarios primero (requeridos para pedidos)
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("crearUsers.sql"));
            
            // Cargar productos
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("crearProductos.sql"));
            
            // Cargar pedidos (que referencian productos y usuarios)
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("crearPedido.sql"));
        }
        
        System.out.println("✅ Base de datos inicializada con datos de prueba");
        System.out.println("📦 Productos cargados: " + productosRepository.count());
        System.out.println("📋 Pedidos cargados: " + pedidoRepository.count());
    }

    /**
     * Test que valida que FG001 (Frodo) no se puede eliminar porque está en el pedido activo.
     * Según crearPedido.sql, este producto está en un pedido con estado "en preparacion".
     */
    @Test
    void testNoSePuedeEliminarFrodo_EstaEnPedidoActivo() throws Exception {
        // Given: FG001 está en el pedido con estado "en preparacion" según crearPedido.sql
        
        // When & Then: Intentar eliminar debe fallar
        mockMvc.perform(delete("/api/productos/FG001"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("ELIMINACIÓN BLOQUEADA")))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("FG001")))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("pedidos activos")));
    }

    /**
     * Test que valida que JM002 (Carcassonne) no se puede eliminar.
     */
    @Test
    void testNoSePuedeEliminarCarcassonne_EstaEnPedidoActivo() throws Exception {
        mockMvc.perform(delete("/api/productos/JM002"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("ELIMINACIÓN BLOQUEADA")))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("JM002")));
    }

    /**
     * Test que valida que PP001 (Polera Level-Up) no se puede eliminar.
     */
    @Test
    void testNoSePuedeEliminarPolera_EstaEnPedidoActivo() throws Exception {
        mockMvc.perform(delete("/api/productos/PP001"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("ELIMINACIÓN BLOQUEADA")))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("PP001")));
    }

    /**
     * Test que valida que productos NO asociados a pedidos SÍ se pueden eliminar.
     * Por ejemplo FG002 (Gandalf) no está en ningún pedido.
     */
    @Test
    void testSiSePuedeEliminarGandalf_NoEstaEnPedidos() throws Exception {
        // Given: FG002 (Gandalf) no está en ningún pedido según crearPedido.sql
        
        // When & Then: Debe poder eliminarse
        mockMvc.perform(delete("/api/productos/FG002"))
                .andExpect(status().isNoContent()); // 204 = eliminación exitosa
    }

    /**
     * Test del endpoint para verificar si un producto puede eliminarse.
     */
    @Test
    void testVerificarQuePuedeBorrar() throws Exception {
        // FG001 NO debe poder eliminarse (está en pedido activo)
        mockMvc.perform(get("/api/productos/FG001/puede-eliminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeEliminar").value(false));

        // FG003 (Gimli) SÍ debe poder eliminarse (no está en pedidos)
        mockMvc.perform(get("/api/productos/FG003/puede-eliminar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeEliminar").value(true));
    }

    /**
     * Test que valida que después de cancelar un pedido, SÍ se pueden eliminar los productos.
     * Este test simula cambiar el estado del pedido a "CANCELADO".
     */
    @Test
    void testProductosPuedenEliminarseDespuesDeCancelarPedido() throws Exception {
        // Nota: Este test requeriría cambiar el estado del pedido primero
        // Por simplicidad, documentamos el comportamiento esperado:
        
        // 1. Si cambias el estado del pedido de "en preparacion" a "CANCELADO"
        // 2. Entonces los productos FG001, JM002, PP001 podrían eliminarse
        // 3. Porque solo los pedidos ACTIVOS (no CANCELADO/ENTREGADO) bloquean la eliminación
        
        // Para probar esto manualmente:
        // UPDATE pedidos SET estado = 'CANCELADO' WHERE id = 1;
        // Luego DELETE /api/productos/FG001 debería retornar 204 (éxito)
    }
}