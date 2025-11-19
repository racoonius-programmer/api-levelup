package com.api.levelup.repository;

import com.api.levelup.model.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, String> {
    
    // Buscar por categoría
    List<Productos> findByCategoria(String categoria);
    
    // Buscar por fabricante
    List<Productos> findByFabricante(String fabricante);
    
    // Buscar por marca
    List<Productos> findByMarca(String marca);
    
    // Buscar por rango de precio
    List<Productos> findByPrecioBetween(Integer precioMin, Integer precioMax);
    
    // Buscar por nombre que contenga texto
    List<Productos> findByNombreContainingIgnoreCase(String nombre);
}