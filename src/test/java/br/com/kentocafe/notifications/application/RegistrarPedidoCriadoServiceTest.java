package br.com.kentocafe.notifications.application;

import br.com.kentocafe.notifications.application.event.PedidoCriadoIntegrationEvent;
import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.application.port.out.AgendadorDeAlertaPort;
import br.com.kentocafe.notifications.application.service.RegistrarPedidoCriadoService;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoPorQuantidadeItens;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarPedidoCriadoServiceTest {
    @Mock
    AcompanhamentoPedidoRepositoryPort repository;
    @Mock
    AgendadorDeAlertaPort agendador;
    PrazoPreparoStrategy estrategia = new PrazoPreparoPorQuantidadeItens(Duration.ofMinutes(5), Duration.ofMinutes(2));

    @Test
    void ignoraEventoDuplicado() {
        when(repository.buscarPorPedidoId(1L)).thenReturn(Optional.of(mock(AcompanhamentoPedido.class)));
        new RegistrarPedidoCriadoService(repository, agendador, estrategia)
                .executar(new PedidoCriadoIntegrationEvent(1L, "Maria", 3, 7L, Instant.now()));
        verifyNoInteractions(agendador);
        verify(repository, never()).salvar(any());
    }
}
