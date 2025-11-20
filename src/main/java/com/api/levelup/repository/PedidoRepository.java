package com.api.levelup.repository;

import com.api.levelup.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
