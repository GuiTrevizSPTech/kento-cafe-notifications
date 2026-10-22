package br.com.kentocafe.notifications.application.event;

public record PedidoStatusAlteradoIntegrationEvent(
        Long pedidoId, String novoStatus // "Pronto" ou "Cancelado"
) {}