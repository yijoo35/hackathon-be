package hackathon.bigone.sunsak.global.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception e) {
        HttpStatus status = mapStatus(e);
        return ResponseEntity.status(status)
                .body(Map.of("message", e.getMessage()));
    }

    private HttpStatus mapStatus(Exception e) {
        if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException) {
            return HttpStatus.UNAUTHORIZED; // 401
        } else if (e instanceof NoSuchElementException) {
            return HttpStatus.NOT_FOUND; // 404
        } else if (e instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST; // 400
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // 기본값 500
    }
}
