package br.com.kentocafe.notifications.application.service;

import br.com.kentocafe.notifications.application.event.PedidoStatusAlteradoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.in.AtualizarStatusPedidoUseCase;
import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import org.springframework.stereotype.Service;

@Service
public class AtualizarStatusPedidoService implements AtualizarStatusPedidoUseCase {
    private final AcompanhamentoPedidoRepositoryPort repository;

    public AtualizarStatusPedidoService(AcompanhamentoPedidoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void executar(PedidoStatusAlteradoIntegrationEvent evento) {
        String codigo = evento.novoStatus() == null ? "" : evento.novoStatus().trim().toUpperCase();
        if (!codigo.equals("PRONTO") && !codigo.equals("CANCELADO")) return; // intermediários não interessam

        AcompanhamentoPedido a = repository.buscarPorPedidoId(evento.pedidoId())
                .orElseThrow(() -> new IllegalStateException("Acompanhamento ainda não existe: " + evento.pedidoId()));
        if (codigo.equals("PRONTO")) a.concluir(); else a.cancelar();
        repository.salvar(a);
    }
}
