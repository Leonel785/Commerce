package com.ecommerce.repository;

import com.ecommerce.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaDesc(Long clienteId);

    /**
     * Trae todos los pedidos con su cliente, sus detalles y el producto de cada detalle.
     * Sirve para la vista admin: "Pedidos de los clientes".
     * Usamos EntityGraph para evitar MultipleBagFetchException con JPQL de multiples FETCH.
     */
    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto"})
    @Query("SELECT p FROM Pedido p ORDER BY p.fecha DESC")
    List<Pedido> findAllWithClienteAndDetalles();
}
