package dev.wsousa.exception;

import org.springframework.web.reactive.function.client.ClientResponse;

public class ApiErrorException extends Exception {

    public ApiErrorException(String message){
        super();
    }

    public ApiErrorException(ClientResponse clientResponse){
    }
}