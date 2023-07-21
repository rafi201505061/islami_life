package life.islami.app;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.exceptions.ValidationFailedException;
import life.islami.app.models.response.Message;

@RestControllerAdvice
public class AppExceptionHandler {

  @ExceptionHandler(value = ValidationFailedException.class)
  public ResponseEntity<List<String>> handleValidationFailedException(ValidationFailedException e) {
    e.printStackTrace();
    return new ResponseEntity<>(e.getMessages(), null,
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = CommonException.class)
  public ResponseEntity<Message> handleCommonException(CommonException e) {
    e.printStackTrace();
    return new ResponseEntity<Message>(new Message(e.getMsg().name(), e.getMsg().getValue()), null,
        e.getStatus());
  }

  @ExceptionHandler(value = JsonMappingException.class)
  public ResponseEntity<Message> handleJsonMappingException(JsonMappingException e) {
    e.printStackTrace();
    return new ResponseEntity<Message>(new Message("MALFORMED_JSON", "Couldn't parse the provided json."), null,
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = JsonProcessingException.class)
  public ResponseEntity<Message> handleJsonProcessingException(JsonProcessingException e) {
    e.printStackTrace();
    return new ResponseEntity<Message>(new Message("MALFORMED_JSON", "Couldn't parse the provided json."), null,
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Message> handleException(Exception e) {
    e.printStackTrace();
    return new ResponseEntity<Message>(new Message(Messages.INTERNAL_SERVER_ERROR.name(), e.getMessage()), null,
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
