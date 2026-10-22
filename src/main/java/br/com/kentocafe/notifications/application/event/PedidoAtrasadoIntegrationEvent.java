package br.com.kentocafe.notifications.application.event;

import java.time.Instant;

// Volta — Monitoramento publica, KentoCafe consome
public record PedidoAtrasadoIntegrationEvent(
        Long pedidoId, String nomeCliente, int quantidadeItens, Instant detectadoEm
) {}
