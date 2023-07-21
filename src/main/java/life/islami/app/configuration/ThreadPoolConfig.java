package life.islami.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

  private static final int THREAD_POOL_SIZE = 10; // Set your desired pool size

  @Bean
  public ExecutorService threadPoolExecutor() {
    return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  }
}
