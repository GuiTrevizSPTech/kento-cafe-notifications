package br.com.kentocafe.notifications.application.service;

import br.com.kentocafe.notifications.application.event.PedidoCriadoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.in.RegistrarPedidoCriadoUseCase;
import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.application.port.out.AgendadorDeAlertaPort;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoStrategy;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RegistrarPedidoCriadoService implements RegistrarPedidoCriadoUseCase {

    private final AcompanhamentoPedidoRepositoryPort repository;
    private final AgendadorDeAlertaPort agendador;
    private final PrazoPreparoStrategy estrategiaPrazo;

    public RegistrarPedidoCriadoService(AcompanhamentoPedidoRepositoryPort repository, AgendadorDeAlertaPort agendador, PrazoPreparoStrategy estrategiaPrazo) {
        this.repository = repository;
        this.agendador = agendador;
        this.estrategiaPrazo = estrategiaPrazo;
    }


    @Override
    public void executar(PedidoCriadoIntegrationEvent evento) {
        if (repository.buscarPorPedidoId(evento.pedidoId()).isPresent()) return; // idempotência (redelivery)

        AcompanhamentoPedido a = AcompanhamentoPedido.criar(evento.pedidoId(), evento.nomeCliente(),
                evento.quantidadeItens(), evento.funcionarioId(), evento.criadoEm(), estrategiaPrazo);

        // agenda ANTES de salvar: se salvar falhar, o retry agenda de novo (timer duplicado é inofensivo,
        // pois marcarComoAtrasado é idempotente); inverter a ordem poderia perder o alerta
        agendador.agendar(a.getPedidoId(), a.getPrazo().milissegundosRestantes(Instant.now()));
        repository.salvar(a);
    }
}
