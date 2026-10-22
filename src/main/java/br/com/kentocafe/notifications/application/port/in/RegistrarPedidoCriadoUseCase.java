package br.com.kentocafe.notifications.application.port.in;

import br.com.kentocafe.notifications.application.event.PedidoCriadoIntegrationEvent;

public interface RegistrarPedidoCriadoUseCase { void executar(PedidoCriadoIntegrationEvent evento); }

