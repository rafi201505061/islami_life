package life.islami.app.exceptions;

import org.springframework.http.HttpStatus;

import life.islami.app.enums.Messages;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonException extends RuntimeException {
  private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
  private Messages msg = Messages.INTERNAL_SERVER_ERROR;

  public CommonException(Messages message) {
    super(message.getValue());
  }

  public CommonException(Messages msg, HttpStatus status) {
    super(msg.getValue());
    this.status = status;
    this.msg = msg;
  }
}
