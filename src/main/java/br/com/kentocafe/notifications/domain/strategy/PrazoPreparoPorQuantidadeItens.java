package br.com.kentocafe.notifications.domain.strategy;

import br.com.kentocafe.notifications.domain.model.PrazoPreparo;

import java.time.Duration;
import java.time.Instant;

public class PrazoPreparoPorQuantidadeItens implements PrazoPreparoStrategy {
    private final Duration base;
    private final Duration porItem;
    public PrazoPreparoPorQuantidadeItens(Duration base, Duration porItem) {
        this.base = base; this.porItem = porItem;
    }
    @Override
    public PrazoPreparo calcular(int quantidadeItens, Instant criadoEm) {
        return new PrazoPreparo(criadoEm.plus(base.plus(porItem.multipliedBy(quantidadeItens))));
    }
}
