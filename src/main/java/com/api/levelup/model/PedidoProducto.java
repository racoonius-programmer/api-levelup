package com.api.levelup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un producto dentro de un pedido en memoria.
 * No está anotado para JPA: los productos se persistirán como JSON dentro
 * de la columna `productos` de la tabla `pedidos`.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoProducto {

    private String codigo;

    private String nombre;

    private Integer cantidad;

    private Double precio;

}
