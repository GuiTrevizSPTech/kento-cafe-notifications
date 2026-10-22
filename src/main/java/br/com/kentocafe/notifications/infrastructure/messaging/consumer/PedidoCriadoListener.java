package br.com.kentocafe.notifications.infrastructure.messaging.consumer;

import br.com.kentocafe.notifications.application.event.PedidoCriadoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.in.RegistrarPedidoCriadoUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoCriadoListener {
    private final RegistrarPedidoCriadoUseCase useCase;
    public PedidoCriadoListener(RegistrarPedidoCriadoUseCase useCase) { this.useCase = useCase; }

    @RabbitListener(queues = "monitoramento.pedido-criado")
    public void receber(PedidoCriadoIntegrationEvent evento) { useCase.executar(evento); }
}
