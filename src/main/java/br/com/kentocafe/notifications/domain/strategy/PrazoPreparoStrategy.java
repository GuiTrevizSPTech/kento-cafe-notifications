package br.com.kentocafe.notifications.domain.strategy;

import br.com.kentocafe.notifications.domain.model.PrazoPreparo;

import java.time.Instant;

public interface PrazoPreparoStrategy {
    PrazoPreparo calcular(int quantidadeItens, Instant criadoEm);
}
