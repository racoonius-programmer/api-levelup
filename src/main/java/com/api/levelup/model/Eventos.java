package com.api.levelup.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class Eventos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String imagenSrc;

    @Column(nullable = false)
    private String imagenAlt;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

}