package br.com.kentocafe.notifications.infrastructure.messaging.consumer;

import br.com.kentocafe.notifications.application.event.PedidoStatusAlteradoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.in.AtualizarStatusPedidoUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoStatusAlteradoListener {
    private final AtualizarStatusPedidoUseCase useCase;
    public PedidoStatusAlteradoListener(AtualizarStatusPedidoUseCase useCase) { this.useCase = useCase; }

    @RabbitListener(queues = "monitoramento.status-alterado")
    public void receber(PedidoStatusAlteradoIntegrationEvent evento) { useCase.executar(evento); }
}
