package dev.wsousa.handler;


import dev.wsousa.exception.BusinessException;
import dev.wsousa.exception.UnsuportedException;
import dev.wsousa.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
@ControllerAdvice
public class ControlHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleAllException(Exception ex, WebRequest request) {
        return getResponseExceptionResponseEntityInternalServerError(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsuportedException.class)
    public ResponseEntity<MessageResponse> handleBadRequestException(Exception ex, WebRequest request) {
        return getResponseExceptionResponseEntityInternalServerError(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageResponse> handleBusinessException(Exception ex, WebRequest request) {
        return getResponseExceptionResponseEntityInternalServerError(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<MessageResponse> getResponseExceptionResponseEntityInternalServerError(Exception ex, WebRequest request, HttpStatus internalServerError) {
        MessageResponse messageResponse = loadMessageResponse(ex, request);
        //telemetryService.trackException(trackingEventBuilder(messageResponse));
        return new ResponseEntity<>(messageResponse, internalServerError);
    }

    private static MessageResponse loadMessageResponse(Exception ex, WebRequest request) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setTimestamp(new Date());
        messageResponse.setMessage(validatedMessageDetails(ex));
        messageResponse.setDetails(request.getDescription(false));
        messageResponse.setCause(splitMessage(validatedMessageDetails(ex)));
        messageResponse.setStackTrace(Arrays.copyOfRange(ex.getStackTrace(), 0, ex.getStackTrace().length > 5 ? 5: ex.getStackTrace().length -1 ));
        return messageResponse;
    }

    private static String validatedMessageDetails(Exception ex) {
        return Objects.nonNull(ex.getMessage()) ? ex.getMessage() : "Não Informado";
    }

    private static String splitMessage(String message) {
        Integer positionStart = 0;
        return  message.contains("|") ? message.split(Pattern.quote("|"))[positionStart] : message;
    }

    private String splitMessageBody(String message) {
        Integer positionEnd = 0;
        return message.contains("|") ? message.split(Pattern.quote("|"))[positionEnd] : "";
    }
}