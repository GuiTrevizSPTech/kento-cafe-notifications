package br.com.kentocafe.notifications.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "acompanhamento_pedido")
public class AcompanhamentoPedidoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;
    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;
    @Column(name = "quantidade_itens", nullable = false)
    private int quantidadeItens;
    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;
    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
    @Column(name = "prazo_limite", nullable = false)
    private Instant prazoLimite;
    @Column(nullable = false)
    private String status;
    @Column(name = "alerta_disparado_em")
    private Instant alertaDisparadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public int getQuantidadeItens() {
        return quantidadeItens;
    }

    public void setQuantidadeItens(int quantidadeItens) {
        this.quantidadeItens = quantidadeItens;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getPrazoLimite() {
        return prazoLimite;
    }

    public void setPrazoLimite(Instant prazoLimite) {
        this.prazoLimite = prazoLimite;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAlertaDisparadoEm() {
        return alertaDisparadoEm;
    }

    public void setAlertaDisparadoEm(Instant alertaDisparadoEm) {
        this.alertaDisparadoEm = alertaDisparadoEm;
    }
}
