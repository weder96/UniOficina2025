package dev.wsousa.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsuportedException  extends RuntimeException {

    public UnsuportedException(String message) {
        super(message);
    }
}