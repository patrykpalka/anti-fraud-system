package antifraud.messaging;

import antifraud.enums.EventNames;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Map<EventNames, Queue> eventQueueMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqMessagePublisher.class);

    @Retryable(
            retryFor = AmqpException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000) // Retry after 2 seconds
    )
    public void sendEvent(EventNames eventType, Object event) {
        if (event == null) {
            LOGGER.error("Message cannot be null for event type: {}", eventType);
            throw new IllegalArgumentException("Message cannot be null for event type: " + eventType);
        }

        Queue queue = eventQueueMap.get(eventType);

        if (queue == null) {
            LOGGER.error("No queue found for event type: {}", eventType);
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }

        sendMessage(queue.getName(), event);
    }

    private void sendMessage(String queueName, Object message) {
        rabbitTemplate.convertAndSend(queueName, message);
        LOGGER.info("Sending event to queue: {}, Message: {}", queueName, message);
    }
}
