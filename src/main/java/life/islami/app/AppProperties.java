package life.islami.app;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AppProperties {
  private final Environment environment;

  public String getProperty(String propertyName) {
    return environment.getProperty(propertyName);
  }
}
