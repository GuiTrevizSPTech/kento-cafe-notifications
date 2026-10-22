package br.com.kentocafe.notifications.infrastructure.persistence;

import br.com.kentocafe.notifications.application.port.out.AcompanhamentoPedidoRepositoryPort;
import br.com.kentocafe.notifications.domain.model.AcompanhamentoPedido;
import br.com.kentocafe.notifications.domain.model.PrazoPreparo;
import br.com.kentocafe.notifications.domain.model.StatusAcompanhamento;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AcompanhamentoPedidoRepositoryAdapter implements AcompanhamentoPedidoRepositoryPort {

    private final AcompanhamentoPedidoJpaRepository jpaRepository;

    public AcompanhamentoPedidoRepositoryAdapter(AcompanhamentoPedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(AcompanhamentoPedido a) {
        AcompanhamentoPedidoJpaEntity entity = jpaRepository.findByPedidoId(a.getPedidoId())
                .orElse(new AcompanhamentoPedidoJpaEntity());
        entity.setPedidoId(a.getPedidoId());
        entity.setNomeCliente(a.getNomeCliente());
        entity.setQuantidadeItens(a.getQuantidadeItens());
        entity.setFuncionarioId(a.getFuncionarioId());
        entity.setPrazoLimite(a.getPrazo().getLimite());
        entity.setStatus(a.getStatus().name());
        entity.setCriadoEm(a.getCriadoEm());
        entity.setAlertaDisparadoEm(a.getAlertaDisparadoEm());
        jpaRepository.save(entity);
    }

    @Override
    public Optional<AcompanhamentoPedido> buscarPorPedidoId(Long pedidoId) {
        return jpaRepository.findByPedidoId(pedidoId).map(e -> AcompanhamentoPedido.reidratar(
                e.getPedidoId(), e.getNomeCliente(), e.getQuantidadeItens(), e.getFuncionarioId(),
                e.getCriadoEm(), new PrazoPreparo(e.getPrazoLimite()), StatusAcompanhamento.valueOf(e.getStatus()),
                e.getAlertaDisparadoEm()));
    }
}
