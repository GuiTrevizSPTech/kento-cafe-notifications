package br.com.kentocafe.notifications.infrastructure.messaging.consumer;

import br.com.kentocafe.notifications.application.port.in.DispararAlertaPedidoAtrasadoUseCase;
import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AlertaPrazoListener {
    private final AcompanhamentoPedidoRepositoryPort repository;
    private final DispararAlertaPedidoAtrasadoUseCase dispararAlerta;

    public AlertaPrazoListener(AcompanhamentoPedidoRepositoryPort repository,
                                    DispararAlertaPedidoAtrasadoUseCase dispararAlerta) {
        this.repository = repository;
        this.dispararAlerta = dispararAlerta;
    }

    @RabbitListener(queues = "monitoramento.alertas")
    public void receber(Long pedidoId) { dispararAlerta.executar(pedidoId); }
}
