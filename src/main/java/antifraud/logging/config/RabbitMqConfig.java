package antifraud.logging.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue transactionQueue() {
        return new Queue("transactionQueue", true);
    }

    @Bean
    public Queue authenticationQueue() {
        return new Queue("authenticationQueue", true);
    }

    @Bean
    public Queue antiFraudQueue() {
        return new Queue("antiFraudQueue", true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue("userQueue", true);
    }
}
