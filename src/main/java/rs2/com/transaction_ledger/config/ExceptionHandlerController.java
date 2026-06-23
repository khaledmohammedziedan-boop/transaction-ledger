package rs2.com.transaction_ledger.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import rs2.com.transaction_ledger.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    /*
     * A simple exception handler to manage my custom exception.
     */

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getHttpStatusCode());
        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("code", ex.getCode());
        map.put("httpStatusCode", status.value());
        return handleExceptionInternal(ex, map, new HttpHeaders(), status, request);
    }
}
