package br.com.kentocafe.notifications.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcompanhamentoPedidoJpaRepository extends JpaRepository<AcompanhamentoPedidoJpaEntity, Long> {
    Optional<AcompanhamentoPedidoJpaEntity> findByPedidoId(Long pedidoId);
}
