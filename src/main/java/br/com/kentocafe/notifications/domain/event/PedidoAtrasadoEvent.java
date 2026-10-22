package br.com.kentocafe.notifications.domain.event;

import java.time.Instant;

public record PedidoAtrasadoEvent(Long pedidoId, String nomeCliente, int quantidadeItens, Instant detectadoEm) {}
