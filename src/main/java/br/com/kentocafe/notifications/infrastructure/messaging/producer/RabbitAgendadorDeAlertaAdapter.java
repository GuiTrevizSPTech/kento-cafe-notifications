package br.com.kentocafe.notifications.infrastructure.messaging.producer;

import br.com.kentocafe.notifications.application.port.out.AgendadorDeAlertaPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitAgendadorDeAlertaAdapter implements AgendadorDeAlertaPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitAgendadorDeAlertaAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void agendar(Long pedidoId, long delayEmMillis) {
        rabbitTemplate.convertAndSend("monitoramento.delayed", "monitoramento.alertas", pedidoId, msg -> {
            msg.getMessageProperties().setHeader("x-delay", Math.toIntExact(delayEmMillis));
            return msg;
        });
    }
}
