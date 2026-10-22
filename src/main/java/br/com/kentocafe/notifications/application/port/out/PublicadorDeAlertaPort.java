package br.com.kentocafe.notifications.application.port.out;

import br.com.kentocafe.notifications.application.event.PedidoAtrasadoIntegrationEvent;

public interface PublicadorDeAlertaPort { void publicarPedidoAtrasado(PedidoAtrasadoIntegrationEvent evento); }
