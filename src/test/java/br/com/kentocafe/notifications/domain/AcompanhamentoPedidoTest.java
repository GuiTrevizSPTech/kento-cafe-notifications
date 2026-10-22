package br.com.kentocafe.notifications.domain;

import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import br.com.kentocafe.notifications.domain.model.StatusAcompanhamento;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoPorQuantidadeItens;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoStrategy;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcompanhamentoPedidoTest {
    private final PrazoPreparoStrategy estrategia =
            new PrazoPreparoPorQuantidadeItens(Duration.ofMinutes(5), Duration.ofMinutes(2));
    private final Instant criadoEm = Instant.parse("2026-10-09T12:00:00Z");

    @Test
    void prazoEhBasePlusPorItem() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        assertEquals(criadoEm.plus(Duration.ofMinutes(11)), a.getPrazo().getLimite()); // 5 + 2*3
    }
    @Test void maisItensDaoMaisTempo() {
        var pequeno = AcompanhamentoPedido.criar(1L, "A", 1, 7L, criadoEm, estrategia);
        var grande  = AcompanhamentoPedido.criar(2L, "B", 10, 7L, criadoEm, estrategia);
        assertTrue(grande.getPrazo().getLimite().isAfter(pequeno.getPrazo().getLimite()));
    }
    @Test void marcaComoAtrasadoEEmiteEvento() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofMinutes(12)));
        assertEquals(1, a.eventosDisparados().size());
    }
    @Test void naoEmiteEventoSeJaConcluidoOuCancelado() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.concluir();
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofHours(1)));
        assertTrue(a.eventosDisparados().isEmpty());
    }

    @Test void naoEmiteEventoSeJaConcluido() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.concluir();
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofHours(1)));
        assertTrue(a.eventosDisparados().isEmpty());
    }

    @Test void naoEmiteEventoSeJaCancelado() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.cancelar();
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofHours(1)));
        assertTrue(a.eventosDisparados().isEmpty());
    }

    @Test void emiteEventoSeAindaEmMonitoramento() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofMinutes(12)));
        assertEquals(1, a.eventosDisparados().size());
    }

    @Test void concluirDepoisDoAlertaMarcaComoConcluido() {
        var a = AcompanhamentoPedido.criar(1L, "Maria", 3, 7L, criadoEm, estrategia);
        a.marcarComoAtrasado(criadoEm.plus(Duration.ofMinutes(12)));
        a.concluir();
        assertEquals(StatusAcompanhamento.CONCLUIDO, a.getStatus());
    }
}
