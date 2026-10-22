package br.com.kentocafe.notifications.domain.model;

import br.com.kentocafe.notifications.domain.event.PedidoAtrasadoEvent;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoStrategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AcompanhamentoPedido {
    private final Long pedidoId;
    private final String nomeCliente;
    private final int quantidadeItens;
    private final Long funcionarioId;
    private final Instant criadoEm;
    private final PrazoPreparo prazo;
    private StatusAcompanhamento status;
    private Instant alertaDisparadoEm;
    private final List<PedidoAtrasadoEvent> eventos = new ArrayList<>();

    private AcompanhamentoPedido(Long pedidoId, String nomeCliente, int quantidadeItens, Long funcionarioId,
                                 Instant criadoEm, PrazoPreparo prazo,
                                 StatusAcompanhamento status, Instant alertaDisparadoEm) {
        // invariantes: evento malformado falha aqui e segue para a DLQ
        if (pedidoId == null) throw new IllegalArgumentException("pedidoId obrigatório");
        if (nomeCliente == null || nomeCliente.isBlank()) throw new IllegalArgumentException("nomeCliente obrigatório");
        if (quantidadeItens < 1) throw new IllegalArgumentException("Pedido sem itens");
        this.pedidoId = pedidoId; this.nomeCliente = nomeCliente; this.quantidadeItens = quantidadeItens;
        this.funcionarioId = funcionarioId; this.criadoEm = criadoEm; this.prazo = prazo;
        this.status = status; this.alertaDisparadoEm = alertaDisparadoEm;
    }

    // Factory: novo acompanhamento, calcula o prazo
    public static AcompanhamentoPedido criar(Long pedidoId, String nomeCliente, int quantidadeItens,
                                             Long funcionarioId, Instant criadoEm, PrazoPreparoStrategy estrategia) {
        return new AcompanhamentoPedido(pedidoId, nomeCliente, quantidadeItens, funcionarioId, criadoEm,
                estrategia.calcular(quantidadeItens, criadoEm), StatusAcompanhamento.EM_MONITORAMENTO, null);
    }

    // Factory: reconstrói um existente, sem recalcular o prazo
    public static AcompanhamentoPedido reidratar(Long pedidoId, String nomeCliente, int quantidadeItens,
                                                 Long funcionarioId, Instant criadoEm, PrazoPreparo prazo,
                                                 StatusAcompanhamento status, Instant alertaDisparadoEm) {
        return new AcompanhamentoPedido(pedidoId, nomeCliente, quantidadeItens, funcionarioId, criadoEm,
                prazo, status, alertaDisparadoEm);
    }

    private boolean emAberto() {
        return status == StatusAcompanhamento.EM_MONITORAMENTO || status == StatusAcompanhamento.ALERTA_DISPARADO;
    }

    // vale também depois do alerta: o pedido atrasado acabou sendo entregue
    public void concluir() { if (emAberto()) status = StatusAcompanhamento.CONCLUIDO; }
    public void cancelar() { if (emAberto()) status = StatusAcompanhamento.CANCELADO; }

    // chamado quando o timer do broker vence; só decide pelo status, nunca pelo relógio local
    public void marcarComoAtrasado(Instant detectadoEm) {
        if (status != StatusAcompanhamento.EM_MONITORAMENTO) return; // idempotente
        status = StatusAcompanhamento.ALERTA_DISPARADO;
        alertaDisparadoEm = detectadoEm;
        eventos.add(new PedidoAtrasadoEvent(pedidoId, nomeCliente, quantidadeItens, detectadoEm));
    }

    public List<PedidoAtrasadoEvent> eventosDisparados() { return List.copyOf(eventos); }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public int getQuantidadeItens() {
        return quantidadeItens;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public PrazoPreparo getPrazo() {
        return prazo;
    }

    public StatusAcompanhamento getStatus() {
        return status;
    }

    public Instant getAlertaDisparadoEm() {
        return alertaDisparadoEm;
    }

    public List<PedidoAtrasadoEvent> getEventos() {
        return eventos;
    }
}
