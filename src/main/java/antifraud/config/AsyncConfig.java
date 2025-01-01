package antifraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // base number of maintained threads
        executor.setMaxPoolSize(20); // maximum number of threads
        executor.setQueueCapacity(50); // waiting tasks queue capacity
        executor.setThreadNamePrefix("AsyncExecutor-"); // prefix of every thread name
        executor.initialize();
        return executor;
    }
}
