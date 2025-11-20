package com.api.levelup.service;

import com.api.levelup.model.Pedido;
import com.api.levelup.model.PedidoProducto;
import com.api.levelup.repository.PedidoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para los pedidos.
 * - calcula total a partir de la lista de productos
 * - serializa/deserializa la lista de productos hacia/desde JSON
 * - operaciones CRUD básicas
 */
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    /** Guarda un pedido: serializa productos a JSON, calcula y setea el total antes de persistir */
    public Pedido guardarPedido(Pedido pedido) {
        // calcular total desde la lista en memoria
        pedido.setTotal(calcularTotal(pedido));

        // serializar lista de productos a JSON para persistir
        try {
            String json = mapper.writeValueAsString(pedido.getProductos() == null ? new ArrayList<>() : pedido.getProductos());
            pedido.setProductosJson(json);
        } catch (Exception e) {
            // en caso de error, guardar array vacío
            pedido.setProductosJson("[]");
        }

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        List<Pedido> lista = pedidoRepository.findAll();
        // deserializar productosJson a lista en cada pedido
        for (Pedido p : lista) {
            populateProductosFromJson(p);
        }
        return lista;
    }

    public Optional<Pedido> obtenerPedidoPorId(Integer id) {
        Optional<Pedido> opt = pedidoRepository.findById(id);
        opt.ifPresent(this::populateProductosFromJson);
        return opt;
    }

    /** Actualiza un pedido existente. Vuelve a calcular el total y serializa productos. */
    public Pedido actualizarPedido(Integer id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el Pedido"));

        existente.setFecha(pedido.getFecha());
        existente.setClienteId(pedido.getClienteId());

        // reemplazamos la lista en memoria y serializamos
        existente.setProductos(pedido.getProductos());
        try {
            String json = mapper.writeValueAsString(pedido.getProductos() == null ? new ArrayList<>() : pedido.getProductos());
            existente.setProductosJson(json);
        } catch (Exception e) {
            existente.setProductosJson("[]");
        }

        existente.setEstado(pedido.getEstado());

        existente.setTotal(calcularTotal(existente));

        return pedidoRepository.save(existente);
    }

    public void eliminarPedido(Integer id) {
        pedidoRepository.deleteById(id);
    }

    /** Actualiza únicamente el estado del pedido */
    public Pedido actualizarEstado(Integer id, String estado) {
        Pedido existente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el Pedido"));
        existente.setEstado(estado);
        return pedidoRepository.save(existente);
    }

    /** Calcula la suma total de los productos del pedido */
    private Double calcularTotal(Pedido pedido) {
        List<PedidoProducto> productos = pedido.getProductos();
        if (productos == null) return 0.0;
        double total = 0.0;
        for (PedidoProducto p : productos) {
            if (p != null && p.getCantidad() != null && p.getPrecio() != null) {
                total += p.getCantidad() * p.getPrecio();
            }
        }
        return total;
    }

    /** Llena el campo transient `productos` desde `productosJson` */
    private void populateProductosFromJson(Pedido pedido) {
        if (pedido.getProductosJson() == null || pedido.getProductosJson().isBlank()) {
            pedido.setProductos(new ArrayList<>());
            return;
        }
        try {
            List<PedidoProducto> list = mapper.readValue(pedido.getProductosJson(), new TypeReference<List<PedidoProducto>>() {});
            pedido.setProductos(list == null ? new ArrayList<>() : list);
        } catch (Exception e) {
            pedido.setProductos(new ArrayList<>());
        }
    }
}
