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
    participant RMQ_In as RabbitMQ (orders.delayed.queue)
    participant Listener as OrderDelayedListener (Infra)
    participant UseCase as NotifyDelayedOrderService (App)
    participant Domain as Order (Domain)
    participant RMQ_Out as RabbitMQNotificationAdapter (Infra)
    participant Exchange as RabbitMQ (notifications.exchange)

    RMQ_In->>Listener: Consome Evento (order_id)
    Listener->>UseCase: execute(order_id)
    
    Note over UseCase,Domain: Regras de Negócio
    UseCase->>Domain: order.isDelayed(5)
    Domain-->>UseCase: true
    
    UseCase->>UseCase: Instancia Notification("Aviso de Preferência")
    
    Note over UseCase,RMQ_Out: Saída de Dados (Port Adapter)
    UseCase->>RMQ_Out: publish(notification)
    RMQ_Out->>Exchange: Publica Mensagem (routingKey: popup)
    
    Exchange-->>RMQ_Out: Ack (Confirmação)
    RMQ_Out-->>UseCase: Sucesso
    UseCase-->>Listener: Sucesso
    Listener-->>RMQ_In: Ack (Mensagem processada com sucesso)
```

## 3. Diagrama de Processo de Negócio (Fluxo Lógico)

```mermaid
flowchart TD
    A([Início: Pedido Criado no Caixa]) --> B[Envia Mensagem para Fila Principal]
    B --> C{Sistema de Preparo}
    
    C -->|Concluído em < 5 min| D([Fim: Pedido Entregue])
    
    C -->|Permanece Pendente| E[Mensagem expira após 5min via TTL/DLX]
    E --> F[Fila de Atrasos: orders.delayed]
    
    F --> G[Microsserviço de Notificação]
    G --> H[Valida Regras de Domínio]
    H --> I[Cria Payload de Popup]
    I --> J[Publica na Fila de Notificações]
    
    J --> K([Fim: Sistema exibe Popup])
```
