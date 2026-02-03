package dev.elder.picpay.service.exceptions;

public class ExternalServiceForbiddenException extends RuntimeException{
    public ExternalServiceForbiddenException(String msg) {
        super(msg);
    }
}
