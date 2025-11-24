package com.api.levelup.service;

import com.api.levelup.model.Productos;
import com.api.levelup.repository.ProductosRepository;
import com.api.levelup.repository.PedidoRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para ProductosService.
 * Se enfoca en validar la lógica de eliminación de productos con reglas de negocio.
 */
class ProductosServiceTest {

    @Mock
    private ProductosRepository productosRepository;
    
    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ProductosService productosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEliminarProducto_Exitoso_CuandoNoEstaEnPedidosActivos() {
        // Given
        String codigo = "TEST001";
        when(productosRepository.existsById(codigo)).thenReturn(true);
        when(pedidoRepository.existePedidoActivoConProducto(codigo)).thenReturn(false);

        // When
        productosService.eliminarProducto(codigo);

        // Then
        verify(productosRepository).deleteById(codigo);
    }

    @Test
    void testEliminarProducto_Falla_CuandoProductoNoExiste() {
        // Given
        String codigo = "INEXISTENTE";
        when(productosRepository.existsById(codigo)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productosService.eliminarProducto(codigo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No existe el Producto con código: INEXISTENTE");
                
        verify(productosRepository, never()).deleteById(any());
        verify(pedidoRepository, never()).existePedidoActivoConProducto(any());
    }

    @Test
    void testEliminarProducto_Falla_CuandoEstaEnPedidosActivos() {
        // Given
        String codigo = "FG001";
        when(productosRepository.existsById(codigo)).thenReturn(true);
        when(pedidoRepository.existePedidoActivoConProducto(codigo)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> productosService.eliminarProducto(codigo))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ELIMINACIÓN BLOQUEADA")
                .hasMessageContaining("asociado a uno o más pedidos activos");
                
        verify(productosRepository, never()).deleteById(any());
    }

    @Test
    void testPuedeEliminarProducto_RetornaTrue_CuandoPuedeEliminarse() {
        // Given
        String codigo = "TEST001";
        when(productosRepository.existsById(codigo)).thenReturn(true);
        when(pedidoRepository.existePedidoActivoConProducto(codigo)).thenReturn(false);

        // When
        boolean resultado = productosService.puedeEliminarProducto(codigo);

        // Then
        assertThat(resultado).isTrue();
    }

    @Test
    void testPuedeEliminarProducto_RetornaFalse_CuandoNoExiste() {
        // Given
        String codigo = "INEXISTENTE";
        when(productosRepository.existsById(codigo)).thenReturn(false);

        // When
        boolean resultado = productosService.puedeEliminarProducto(codigo);

        // Then
        assertThat(resultado).isFalse();
    }

    @Test
    void testPuedeEliminarProducto_RetornaFalse_CuandoEstaEnPedidosActivos() {
        // Given
        String codigo = "FG001";
        when(productosRepository.existsById(codigo)).thenReturn(true);
        when(pedidoRepository.existePedidoActivoConProducto(codigo)).thenReturn(true);

        // When
        boolean resultado = productosService.puedeEliminarProducto(codigo);

        // Then
        assertThat(resultado).isFalse();
    }

    @Test
    void testGuardarProducto() {
        // Given
        Productos producto = new Productos("TEST001", "Producto Test", "/img/test.jpg", 
                                          10000, "Test Corp", "Test Dist", "Test Brand", 
                                          "Plástico", "Producto de prueba", "test.html", "test");
        when(productosRepository.save(producto)).thenReturn(producto);

        // When
        Productos resultado = productosService.guardarProducto(producto);

        // Then
        assertThat(resultado).isEqualTo(producto);
        verify(productosRepository).save(producto);
    }

    @Test
    void testListarProductos() {
        // Given
        List<Productos> productos = Arrays.asList(
            new Productos("FG001", "Frodo", "/img/frodo.png", 29990, "LOTR Toys", "LOTR Dist", "LOTR", "Plástico", "Figura de Frodo", "frodo.html", "figuras"),
            new Productos("JM001", "Catan", "/img/catan.jpg", 29990, "Catan Studio", "Catan Dist", "Catan", "Cartón", "Juego Catan", "catan.html", "juegos_de_mesa")
        );
        when(productosRepository.findAll()).thenReturn(productos);

        // When
        List<Productos> resultado = productosService.listarProductos();

        // Then
        assertThat(resultado).hasSize(2).containsExactlyElementsOf(productos);
    }
}