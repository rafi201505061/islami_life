package life.islami.app.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ValidationFailedException extends RuntimeException {
  private List<String> messages = new ArrayList<>();

  public ValidationFailedException(List<String> messages) {
    this.messages = messages;
  }
}
