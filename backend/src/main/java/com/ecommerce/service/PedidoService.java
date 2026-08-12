package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarritoService carritoService;

    public Pedido crearPedido(Long clienteId) {
        Carrito carrito = carritoService.obtenerCarritoPorCliente(clienteId);

        if (carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("El carrito esta vacio");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTotal(carrito.getTotal());
        pedido.setEstado(Pedido.Estado.PENDIENTE);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (DetalleCarrito dc : carrito.getDetalles()) {
            Producto producto = dc.getProducto();
            producto.setStock(producto.getStock() - dc.getCantidad());
            productoRepository.save(producto);

            DetallePedido detallePedido = new DetallePedido(
                    pedidoGuardado, producto, dc.getCantidad()
            );
            detallePedido.setPrecioUnitario(dc.getPrecioUnitario());
            detallePedido.calcularSubtotal();
            pedidoGuardado.addDetalle(detallePedido);
            detallePedidoRepository.save(detallePedido);
        }

        carritoService.vaciarCarrito(clienteId);

        return pedidoGuardado;
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaDesc(clienteId);
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllWithClienteAndDetalles();
    }

    public Pedido actualizarEstado(Long id, Pedido.Estado estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }
}
