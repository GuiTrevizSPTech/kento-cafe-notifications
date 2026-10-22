package br.com.kentocafe.notifications.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class PrazoPreparo {
    private final Instant limite;
    public PrazoPreparo(Instant limite) { this.limite = Objects.requireNonNull(limite); }
    public Instant getLimite() { return limite; }
    public long milissegundosRestantes(Instant agora) {
        return Math.max(0, Duration.between(agora, limite).toMillis());
    }
    @Override public boolean equals(Object o) { return o instanceof PrazoPreparo p && limite.equals(p.limite); }
    @Override public int hashCode() { return limite.hashCode(); }
}
