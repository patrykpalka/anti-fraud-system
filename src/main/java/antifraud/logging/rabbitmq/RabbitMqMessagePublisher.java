package antifraud.logging.rabbitmq;

import antifraud.enums.EventNames;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RabbitMqMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue transactionQueue;
    private final Queue authenticationQueue;
    private final Queue antiFraudQueue;
    private final Queue userQueue;
    private Map<String, Queue> eventQueueMap;

    @PostConstruct
    public void init() {
        eventQueueMap = Map.of(
                EventNames.TRANSACTION.toString(), transactionQueue,
                EventNames.AUTHENTICATION.toString(), authenticationQueue,
                EventNames.ANTIFRAUD.toString(), antiFraudQueue,
                EventNames.USER.toString(), userQueue
        );
    }

    // Method to send a message to RabbitMQ
    @Retryable(
            value = AmqpException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000) // Retry after 2 seconds
    )
    public void sendEvent(String eventType, Object event) {
        Queue queue = eventQueueMap.get(eventType);

        if (queue == null) {
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }

        sendMessage(queue.getName(), event);
    }

    private void sendMessage(String queueName, Object message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
