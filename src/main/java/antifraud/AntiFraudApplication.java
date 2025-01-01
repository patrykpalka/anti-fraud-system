package antifraud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class AntiFraudApplication {
    private static final Logger logger = LoggerFactory.getLogger(AntiFraudApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Anti-Fraud System");
        SpringApplication.run(AntiFraudApplication.class, args);
        logger.info("Anti-Fraud System started successfully");
    }
}