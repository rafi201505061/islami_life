package life.islami.app.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import life.islami.app.services.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Scheduler {

  private final UserService userService;

  @Scheduled(fixedRate = 6000000)
  public void myScheduledTask() {
    userService.deactivateSubscriptions();
  }
}
