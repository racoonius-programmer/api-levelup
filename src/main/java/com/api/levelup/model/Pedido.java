package com.api.levelup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pedido adaptada para almacenar el array de productos como JSON
 * dentro de la propia fila de `pedidos` (columna `productos`).
 *
 * Implementación:
 * - `productos` (transient): lista usada por la API en memoria (serializada/deserializada por el servicio)
 * - `productosJson` : columna en la BD con tipo JSON que contiene la representación serializada
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fecha; // ISO 8601

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    /**
     * Lista de productos para uso en la API. No persistida directamente por JPA.
     * El contenido se almacena en `productosJson`.
     */
    @Transient
    private List<PedidoProducto> productos = new ArrayList<>();

    /** Campo JSON que se persiste en BD. Contiene la representación de `productos`. */
    @Column(name = "productos", columnDefinition = "JSON", nullable = true)
    @JsonIgnore
    private String productosJson;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = true)
    private Double total;

}
