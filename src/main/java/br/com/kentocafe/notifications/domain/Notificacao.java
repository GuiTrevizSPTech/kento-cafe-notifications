package br.com.kentocafe.notifications.domain;

import br.com.kentocafe.notifications.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacao {
    private final UUID id;
    private final String message;
    private final LocalDateTime generatedAt;

    private Notificacao(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.message = builder.message;
        this.generatedAt = builder.generatedAt != null ? builder.generatedAt : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public static class Builder {
        private UUID id;
        private String message;
        private LocalDateTime generatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder generatedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public Notificacao build() {
            if (this.message == null || this.message.isBlank()) {
                throw new DomainException("A mensagem da notificação é obrigatória");
            }
            return new Notificacao(this);
        }
    }
}
