package br.com.kentocafe.notifications.infrastructure.messaging.producer;

import br.com.kentocafe.notifications.application.event.PedidoAtrasadoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.out.PublicadorDeAlertaPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitPublicadorDeAlertaAdapter implements PublicadorDeAlertaPort {
    private final RabbitTemplate rabbitTemplate;

    public RabbitPublicadorDeAlertaAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarPedidoAtrasado(PedidoAtrasadoIntegrationEvent evento) {
        rabbitTemplate.convertAndSend("monitoramento.events", "pedido.atrasado", evento);
    }
}
