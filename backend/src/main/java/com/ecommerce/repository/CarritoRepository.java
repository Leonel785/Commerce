package com.ecommerce.repository;

import com.ecommerce.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByClienteId(Long clienteId);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.producto WHERE c.cliente.id = :clienteId")
    Optional<Carrito> findByClienteIdWithDetalles(@Param("clienteId") Long clienteId);
}
