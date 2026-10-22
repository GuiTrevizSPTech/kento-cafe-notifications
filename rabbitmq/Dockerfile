FROM rabbitmq:4.1-management
ARG PLUGIN_VERSION=4.1.0
ADD --chown=rabbitmq:rabbitmq \
  https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v${PLUGIN_VERSION}/rabbitmq_delayed_message_exchange-${PLUGIN_VERSION}.ez \
  /opt/rabbitmq/plugins/
RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange