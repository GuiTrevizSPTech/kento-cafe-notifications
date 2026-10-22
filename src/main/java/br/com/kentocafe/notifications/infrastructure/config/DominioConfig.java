package br.com.kentocafe.notifications.infrastructure.config;

import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoPorQuantidadeItens;
import br.com.kentocafe.notifications.domain.strategy.PrazoPreparoStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DominioConfig {
    @Bean
    public PrazoPreparoStrategy prazoPreparoStrategy(
            @Value("${monitoramento.prazo.base}") Duration base,
            @Value("${monitoramento.prazo.por-item}") Duration porItem) {
        return new PrazoPreparoPorQuantidadeItens(base, porItem);
    }
}