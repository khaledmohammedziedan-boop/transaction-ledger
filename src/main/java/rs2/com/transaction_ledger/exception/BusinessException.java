package rs2.com.transaction_ledger.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private int httpStatusCode;
    public BusinessException(String stackTrace) {
        super(stackTrace);
    }

    public BusinessException(String code, String message, int httpStatusCode) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


}
