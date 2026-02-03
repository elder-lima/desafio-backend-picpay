package dev.elder.picpay.service.exceptions;

public class TransferNotAuthorizedException extends RuntimeException{
    public TransferNotAuthorizedException(String msg) {
        super(msg);
    }
}
