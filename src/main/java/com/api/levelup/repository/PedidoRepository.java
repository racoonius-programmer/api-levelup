package com.api.levelup.repository;

import com.api.levelup.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Pedido.
 * Se extiende JpaRepository para disponer de métodos CRUD estándar.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

}
