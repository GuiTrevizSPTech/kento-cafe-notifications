package br.com.kentocafe.notifications.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqTopologyConfig {

    // ── Entrada: eventos publicados pelo KentoCafe ──
    @Bean
    public TopicExchange pedidoEventsExchange() { return new TopicExchange("pedido.events"); }

    @Bean public Queue filaPedidoCriado() {
        return QueueBuilder.durable("monitoramento.pedido-criado")
                .deadLetterExchange("monitoramento.dlx")
                .deadLetterRoutingKey("monitoramento.pedido-criado.dlq").build();
    }
    @Bean public Queue filaStatusAlterado() {
        return QueueBuilder.durable("monitoramento.status-alterado")
                .deadLetterExchange("monitoramento.dlx")
                .deadLetterRoutingKey("monitoramento.status-alterado.dlq").build();
    }
    @Bean public Binding bindingPedidoCriado() {
        return BindingBuilder.bind(filaPedidoCriado()).to(pedidoEventsExchange()).with("pedido.criado");
    }
    @Bean public Binding bindingStatusAlterado() {
        return BindingBuilder.bind(filaStatusAlterado()).to(pedidoEventsExchange()).with("pedido.status.alterado");
    }

    // ── Prazo: exchange com atraso (plugin) ──
    @Bean public DirectExchange delayedExchange() {
        DirectExchange ex = new DirectExchange("monitoramento.delayed", true, false);
        ex.setDelayed(true);
        return ex;
    }
    @Bean public Queue filaDeAlertas() {
        return QueueBuilder.durable("monitoramento.alertas")
                .deadLetterExchange("monitoramento.dlx")
                .deadLetterRoutingKey("monitoramento.alertas.dlq").build();
    }
    @Bean public Binding bindingAlertas() {
        return BindingBuilder.bind(filaDeAlertas()).to(delayedExchange()).with("monitoramento.alertas");
    }

    // ── DLQs: mensagens que falharam depois dos retries ──
    @Bean public DirectExchange dlx() { return new DirectExchange("monitoramento.dlx"); }

    @Bean public Queue pedidoCriadoDlq()    { return QueueBuilder.durable("monitoramento.pedido-criado.dlq").build(); }
    @Bean public Queue statusAlteradoDlq()  { return QueueBuilder.durable("monitoramento.status-alterado.dlq").build(); }
    @Bean public Queue alertasDlq()         { return QueueBuilder.durable("monitoramento.alertas.dlq").build(); }

    @Bean public Binding bindingPedidoCriadoDlq() {
        return BindingBuilder.bind(pedidoCriadoDlq()).to(dlx()).with("monitoramento.pedido-criado.dlq");
    }
    @Bean public Binding bindingStatusAlteradoDlq() {
        return BindingBuilder.bind(statusAlteradoDlq()).to(dlx()).with("monitoramento.status-alterado.dlq");
    }
    @Bean public Binding bindingAlertasDlq() {
        return BindingBuilder.bind(alertasDlq()).to(dlx()).with("monitoramento.alertas.dlq");
    }

    // ── Saída: eventos de volta para o KentoCafe ──
    @Bean public TopicExchange monitoramentoEventsExchange() { return new TopicExchange("monitoramento.events"); }

    @Bean public Jackson2JsonMessageConverter conversor() { return new Jackson2JsonMessageConverter(); }
}
