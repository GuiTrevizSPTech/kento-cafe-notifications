package br.com.kentocafe.notifications.application.port.out;

import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;

import java.util.Optional;

public interface AcompanhamentoPedidoRepositoryPort {
    void salvar(AcompanhamentoPedido acompanhamento);
    Optional<AcompanhamentoPedido> buscarPorPedidoId(Long pedidoId);
}
