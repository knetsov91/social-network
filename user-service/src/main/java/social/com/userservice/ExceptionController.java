package social.com.userservice;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import social.com.userservice.exceptions.ExpiredTokenException;
import social.com.userservice.web.dto.ErrorResponse;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<ErrorResponse> handleExpiredTokenException(HttpServletRequest req, HttpServletResponse resp, ExpiredTokenException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Expired Token");
        errorResponse.setCode("401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handle(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode("400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handle(FeignException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Communication error");
        errorResponse.setCode(String.valueOf(e.status()));
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
        String collect = ex.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("\n"));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(collect);
        errorResponse.setCode("400");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Something went wrong");
        errorResponse.setCode("500");

        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}