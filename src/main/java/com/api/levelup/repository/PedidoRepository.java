package com.api.levelup.repository;

import com.api.levelup.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Pedido.
 * Se extiende JpaRepository para disponer de métodos CRUD estándar.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    /**
     * Busca todos los pedidos de un cliente específico.
     * @param clienteId ID del cliente
     * @return Lista de pedidos del cliente
     */
    List<Pedido> findByClienteId(Integer clienteId);

    /**
     * Verifica si existen pedidos activos para un cliente específico.
     * Se considera activo cualquier pedido que no esté en estado "CANCELADO" o "ENTREGADO".
     * @param clienteId ID del cliente
     * @return true si existen pedidos activos, false en caso contrario
     */
    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.clienteId = :clienteId AND p.estado NOT IN ('CANCELADO', 'ENTREGADO')")
    boolean existePedidoActivoPorClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Verifica si existen pedidos activos que contengan un producto específico.
     * Utiliza JPQL con LIKE para buscar el código del producto en el campo JSON.
     * 
     * EXPLICACIÓN DE LA LÓGICA:
     * - Busca pedidos con estado diferente a 'CANCELADO' y 'ENTREGADO'
     * - Dentro de esos pedidos, busca en el campo productosJson si contiene el código del producto
     * - Usa LIKE para encontrar el patrón JSON que contiene el código del producto
     * - JPQL maneja automáticamente la conversión a Boolean
     * 
     * @param codigoProducto código del producto a verificar
     * @return true si existen pedidos activos con el producto, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Pedido p " +
           "WHERE p.estado NOT IN ('CANCELADO', 'ENTREGADO') " +
           "AND (p.productosJson LIKE CONCAT('%\"codigo\":\"', :codigoProducto, '\"%') " +
           "OR p.productosJson LIKE CONCAT('%\"codigo\": \"', :codigoProducto, '\"%'))")
    boolean existePedidoActivoConProducto(@Param("codigoProducto") String codigoProducto);

}
