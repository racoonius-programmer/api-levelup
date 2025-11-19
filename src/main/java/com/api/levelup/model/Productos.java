package com.api.levelup.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Productos {
    @Id
    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String imagen;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private String fabricante;

    @Column(nullable = false)
    private String distribuidor;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String material;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private String enlace;

    @Column(nullable = false)
    private String categoria;
}