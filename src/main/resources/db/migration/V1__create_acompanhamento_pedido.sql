CREATE SCHEMA IF NOT EXISTS monitoramento_pedidos;
USE monitoramento_pedidos;

CREATE TABLE acompanhamento_pedido (
                                       id                   BIGINT       NOT NULL AUTO_INCREMENT,
                                       pedido_id            BIGINT       NOT NULL,
                                       nome_cliente         VARCHAR(45)  NOT NULL,
                                       quantidade_itens     INT          NOT NULL,
                                       funcionario_id       BIGINT       NOT NULL,
                                       criado_em            DATETIME(6)  NOT NULL,
                                       prazo_limite         DATETIME(6)  NOT NULL,
                                       status               VARCHAR(20)  NOT NULL,
                                       alerta_disparado_em  DATETIME(6)  NULL,
                                       PRIMARY KEY (id),
                                       UNIQUE KEY uk_acompanhamento_pedido_id (pedido_id)
);