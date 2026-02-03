package dev.elder.picpay.service.exceptions;

// Exceção específica para erros de cliente/HTTP
public class NotificationClientException extends NotificationException{
    private final int statusCode;

    public NotificationClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
