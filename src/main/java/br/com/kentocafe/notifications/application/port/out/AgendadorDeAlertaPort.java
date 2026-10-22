package br.com.kentocafe.notifications.application.port.out;

public interface AgendadorDeAlertaPort { void agendar(Long pedidoId, long delayEmMillis); }

