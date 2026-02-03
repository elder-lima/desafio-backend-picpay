package dev.elder.picpay.service.exceptions;

public class TransferNotAllowedForWalletException extends RuntimeException{
    public TransferNotAllowedForWalletException(String msg) {
        super(msg);
    }
}
