package posts.social.com.postservice.web;

import io.sentry.Sentry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLock(OptimisticLockingFailureException ex) {
        Sentry.captureException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Resource was modified by another request, please retry.");
    }
}
