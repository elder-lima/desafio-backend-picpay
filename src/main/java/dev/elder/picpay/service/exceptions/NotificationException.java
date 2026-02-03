package dev.elder.picpay.service.exceptions;

// Exceção base para notificações
public class NotificationException extends RuntimeException{
    public NotificationException(String msg) {
        super(msg);
    }
    public NotificationException(String msg, Throwable causaErro) {
        super(msg, causaErro);
    }
}
