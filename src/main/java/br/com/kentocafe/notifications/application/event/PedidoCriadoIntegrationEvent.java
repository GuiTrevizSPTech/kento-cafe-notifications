package br.com.kentocafe.notifications.application.event;

import java.time.Instant;

// Ida — KentoCafe publica, Monitoramento consome
public record PedidoCriadoIntegrationEvent(
        Long pedidoId, String nomeCliente, int quantidadeItens,
        Long funcionarioId, Instant criadoEm
) {}
