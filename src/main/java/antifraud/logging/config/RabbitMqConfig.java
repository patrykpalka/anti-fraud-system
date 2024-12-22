package antifraud.logging.config;

import antifraud.enums.EventNames;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Map<EventNames, Queue> eventQueueMap() {
        return Map.of(
                EventNames.TRANSACTION, new Queue("transactionQueue", true),
                EventNames.AUTHENTICATION, new Queue("authenticationQueue", true),
                EventNames.ANTIFRAUD, new Queue("antiFraudQueue", true),
                EventNames.USER, new Queue("userQueue", true)
        );
    }
}
