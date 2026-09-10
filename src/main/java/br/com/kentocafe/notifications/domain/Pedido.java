package br.com.kentocafe.notifications.domain;

import br.com.kentocafe.notifications.domain.exception.DomainException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Pedido {
    private final Long id;
    private final LocalDateTime dtHrPedido;
    private final LocalDateTime dtHrPronto;
    private PedidoStatus status;

    private Pedido(Builder builder) {
        this.id = builder.id;
        this.dtHrPedido = builder.dtHrPedido;
        this.dtHrPronto = builder.dtHrPronto;
        this.status = builder.status;
    }

    public boolean isDelayed(int thresholdMinutes, LocalDateTime currentTime) {
        if (this.dtHrPronto != null) {
            return false;
        }

        String nomeStatus = this.status.getNome();
        if (nomeStatus.equalsIgnoreCase("Pronto") || nomeStatus.equalsIgnoreCase("Cancelado")) {
            return false;
        }

        Duration duration = Duration.between(this.dtHrPedido, currentTime);
        return duration.toMinutes() >= thresholdMinutes;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDtHrPedido() {
        return dtHrPedido;
    }

    public LocalDateTime getDtHrPronto() {
        return dtHrPronto;
    }

    public PedidoStatus getStatus() {
        return status;
    }

    public static class Builder {
        private Long id;
        private LocalDateTime dtHrPedido;
        private LocalDateTime dtHrPronto;
        private PedidoStatus status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder dtHrPedido(LocalDateTime dtHrPedido) {
            this.dtHrPedido = dtHrPedido;
            return this;
        }

        public Builder dtHrPronto(LocalDateTime dtHrPronto) {
            this.dtHrPronto = dtHrPronto;
            return this;
        }

        public Builder status(PedidoStatus status) {
            this.status = status;
            return this;
        }

        public Pedido build() {
            if (this.id == null) {
                throw new DomainException("O ID do pedido é obrigatório.");
            }
            if (this.dtHrPedido == null) {
                throw new DomainException("A data do pedido é obrigatória.");
            }
            if (this.status == null) {
                throw new DomainException("O status do pedido é obrigatório.");
            }

            return new Pedido(this);
        }
    }
}
