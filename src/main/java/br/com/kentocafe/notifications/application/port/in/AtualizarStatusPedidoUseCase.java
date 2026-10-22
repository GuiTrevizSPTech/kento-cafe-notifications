package br.com.kentocafe.notifications.application.port.in;

import br.com.kentocafe.notifications.application.event.PedidoStatusAlteradoIntegrationEvent;

public interface AtualizarStatusPedidoUseCase { void executar(PedidoStatusAlteradoIntegrationEvent evento); }

