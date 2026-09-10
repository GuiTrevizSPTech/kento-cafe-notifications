package br.com.kentocafe.notifications.domain;

import br.com.kentocafe.notifications.domain.exception.DomainException;

public class PedidoStatus {
    private final Long id;
    private final String nome;

    private PedidoStatus(Builder builder) {
        this.id = builder.id;
        this.nome = builder.nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public static class Builder {
        private Long id;
        private String nome;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public PedidoStatus build() {
            if (this.id == null) {
                throw new DomainException("O ID do status é obrigatório");
            }

            if (this.nome == null) {
                throw new DomainException("O nome do status é obrigatório");
            }

            return new PedidoStatus(this);
        }
    }
}
