package social.com.authservice.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import social.com.authservice.web.dto.ExceptionResponse;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> exception(Exception e) {
        log.error(e.getStackTrace().toString());
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(e.getMessage());
        exceptionResponse.setStausCode(500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
}
