package com.api.levelup.service;

import com.api.levelup.model.Productos;
import com.api.levelup.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductosService {

    @Autowired
    private ProductosRepository productosRepository;

    public Productos guardarProducto(Productos producto) {
        return productosRepository.save(producto);
    }

    public List<Productos> listarProductos() {
        return productosRepository.findAll();
    }

    public Optional<Productos> obtenerProductoPorCodigo(String codigo) {
        return productosRepository.findById(codigo);
    }

    public Productos actualizarProducto(String codigo, Productos producto) {
        return productosRepository.findById(codigo)
                .map(p -> {
                    p.setNombre(producto.getNombre());
                    p.setImagen(producto.getImagen());
                    p.setPrecio(producto.getPrecio());
                    p.setFabricante(producto.getFabricante());
                    p.setDistribuidor(producto.getDistribuidor());
                    p.setMarca(producto.getMarca());
                    p.setMaterial(producto.getMaterial());
                    p.setDescripcion(producto.getDescripcion());
                    p.setEnlace(producto.getEnlace());
                    p.setCategoria(producto.getCategoria());
                    return productosRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + codigo));
    }

    public void eliminarProducto(String codigo) {
        productosRepository.deleteById(codigo);
    }

    public List<Productos> buscarPorCategoria(String categoria) {
        return productosRepository.findByCategoria(categoria);
    }

    public List<Productos> buscarPorFabricante(String fabricante) {
        return productosRepository.findByFabricante(fabricante);
    }

    public List<Productos> buscarPorMarca(String marca) {
        return productosRepository.findByMarca(marca);
    }

    public List<Productos> buscarPorRangoPrecio(Integer precioMin, Integer precioMax) {
        return productosRepository.findByPrecioBetween(precioMin, precioMax);
    }

    public List<Productos> buscarPorNombre(String nombre) {
        return productosRepository.findByNombreContainingIgnoreCase(nombre);
    }
}