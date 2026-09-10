# Arquitetura do Microsserviço de Notificações - Kento Café

Este documento detalha o design arquitetural, o fluxo de comunicação e as regras de negócio do serviço de notificações utilizando Clean Architecture.

## 1. Diagrama de Classes (Estrutura Arquitetural)

```mermaid
classDiagram
    %% Camada de Domínio (Core - Independente)
    namespace Domain {
        class Pedido {
            -Long id
            -LocalDateTime dtHrPedido
            -LocalDateTime dtHrPronto
            -PedidoStatus status
            +isAtrasado(int limiteMinutos, LocalDateTime horaAtual) boolean
        }
        class PedidoStatus {
            -Long id
            -String nome
        }
        class Notificacao {
            -UUID id
            -String mensagem
            -LocalDateTime geradaEm
        }
        class DomainException {
            <<RuntimeException>>
            +DomainException(String mensagem)
        }
    }

    %% Camada de Aplicação (Casos de Uso e Portas)
    namespace Application {
        class NotificarPedidoAtrasadoUseCase {
            <<interface>>
            +executar(Long pedidoId)
        }
        class NotificarPedidoAtrasadoService {
            -NotificacaoPublisherPort publisher
            -PedidoRepositoryPort pedidoRepository
            +executar(Long pedidoId)
        }
        class NotificacaoPublisherPort {
            <<interface>>
            +publicar(Notificacao notificacao)
        }
        class PedidoRepositoryPort {
            <<interface>>
            +buscarPorId(Long pedidoId) Pedido
        }
    }

    %% Camada de Infraestrutura
    namespace Infrastructure {
        class RabbitMQNotificacaoAdapter {
            -RabbitTemplate rabbitTemplate
            +publicar(Notificacao notificacao)
        }
        class DatabasePedidoAdapter {
            +buscarPorId(Long pedidoId) Pedido
        }
        class PedidoAtrasadoListener {
            -NotificarPedidoAtrasadoUseCase useCase
            +aoReceberEventoAtraso(Message mensagem)
        }
    }

    %% Relacionamentos
    NotificarPedidoAtrasadoUseCase <|.. NotificarPedidoAtrasadoService : Implementa
    NotificacaoPublisherPort <|.. RabbitMQNotificacaoAdapter : Implementa
    PedidoRepositoryPort <|.. DatabasePedidoAdapter : Implementa
    
    NotificarPedidoAtrasadoService --> NotificacaoPublisherPort : Usa
    NotificarPedidoAtrasadoService --> PedidoRepositoryPort : Usa
    PedidoAtrasadoListener --> NotificarPedidoAtrasadoUseCase : Injeta
    
    NotificarPedidoAtrasadoService ..> Pedido : Usa
    NotificarPedidoAtrasadoService ..> Notificacao : Cria
    Pedido ..> DomainException : Lança (via Builder)
    Notificacao ..> DomainException : Lança (via Builder)
    PedidoStatus ..> DomainException : Lança (via Builder)
```

## 2. Diagrama de Sequência (Fluxo de Execução)

```mermaid
sequenceDiagram
    participant RMQ_In as RabbitMQ (pedidos.atrasados.queue)
    participant Listener as PedidoAtrasadoListener (Infra)
    participant UseCase as NotificarPedidoAtrasadoService (App)
    participant Domain as Pedido (Domain)
    participant RMQ_Out as RabbitMQNotificacaoAdapter (Infra)
    participant Exchange as RabbitMQ (notificacoes.exchange)

    RMQ_In->>Listener: Consome Evento (pedido_id)
    Listener->>UseCase: executar(pedido_id)
    
    Note over UseCase,Domain: Regras de Negócio e Validações
    UseCase->>Domain: pedido.isAtrasado(5)
    
    alt isAtrasado == true
        Domain-->>UseCase: true
        UseCase->>UseCase: Instancia Notificacao("Aviso de Preferência")
        Note over UseCase,RMQ_Out: Saída de Dados (Port Adapter)
        UseCase->>RMQ_Out: publicar(notificacao)
        RMQ_Out->>Exchange: Publica Mensagem (routingKey: popup)
        Exchange-->>RMQ_Out: Ack (Confirmação)
        RMQ_Out-->>UseCase: Sucesso
    else isAtrasado == false
        Domain-->>UseCase: false
        Note over UseCase: Processo encerrado silenciosamente
    end
    
    UseCase-->>Listener: Retorno
    Listener-->>RMQ_In: Ack (Mensagem processada)
```

## 3. Diagrama de Processo de Negócio (Fluxo Lógico)

```mermaid
flowchart TD
    A([Início: Pedido Criado no Caixa]) --> B[Envia Mensagem para Fila Principal]
    B --> C{Sistema de Preparo}
    
    C -->|Concluído em < 5 min| D([Fim: Pedido Entregue])
    
    C -->|Permanece Pendente| E[Mensagem expira após 5min via TTL/DLX]
    E --> F[Fila de Atrasos: pedidos.atrasados]
    
    F --> G[Microsserviço de Notificação]
    G --> H{Valida: isAtrasado?}
    
    H -->|Não| L([Fim: Falso Positivo / Já Finalizado])
    H -->|Sim| I[Cria Payload de Popup / Notificação]
    
    I --> J[Publica na Fila de Notificações]
    J --> K([Fim: Sistema exibe Popup na Cozinha/Caixa])
```
