package br.com.kentocafe.notifications.application.service;

import br.com.kentocafe.notifications.application.event.PedidoAtrasadoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.in.DispararAlertaPedidoAtrasadoUseCase;
import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.application.port.out.PublicadorDeAlertaPort;
import br.com.kentocafe.notifications.domain.event.PedidoAtrasadoEvent;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DispararAlertaPedidoAtrasadoService implements DispararAlertaPedidoAtrasadoUseCase {

    private final AcompanhamentoPedidoRepositoryPort repository;
    private final PublicadorDeAlertaPort publicador;

    public DispararAlertaPedidoAtrasadoService(AcompanhamentoPedidoRepositoryPort repository, PublicadorDeAlertaPort publicador) {
        this.repository = repository;
        this.publicador = publicador;
    }

    @Override
    public void executar(Long pedidoId) {
        AcompanhamentoPedido a = repository.buscarPorPedidoId(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Acompanhamento não encontrado: " + pedidoId));

        a.marcarComoAtrasado(Instant.now());
        List<PedidoAtrasadoEvent> eventos = a.eventosDisparados();
        if (eventos.isEmpty()) return; // já concluído, cancelado ou alertado: nada a fazer

        // domain event → integration event: a tradução acontece aqui, na camada de aplicação
        eventos.forEach(e -> publicador.publicarPedidoAtrasado(new PedidoAtrasadoIntegrationEvent(
                e.pedidoId(), e.nomeCliente(), e.quantidadeItens(), e.detectadoEm())));

        repository.salvar(a); // salva DEPOIS de publicar: melhor alertar duas vezes do que nunca
    }
}
