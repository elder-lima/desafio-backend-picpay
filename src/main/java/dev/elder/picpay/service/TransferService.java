package dev.elder.picpay.service;

import dev.elder.picpay.dto.request.TransferDto;
import dev.elder.picpay.entity.Transfer;
import dev.elder.picpay.entity.Wallet;
import dev.elder.picpay.repository.TransferRepository;
import dev.elder.picpay.repository.WalletRepository;
import dev.elder.picpay.service.exceptions.InsufficientBalanceException;
import dev.elder.picpay.service.exceptions.ResourceNotFoundException;
import dev.elder.picpay.service.exceptions.TransferNotAllowedForWalletException;
import dev.elder.picpay.service.exceptions.TransferNotAuthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class TransferService {

    private final NotificationService notificationService;
    private final AuthorizationService authorizationService;
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    public TransferService(NotificationService notificationService, AuthorizationService authorizationService, TransferRepository transferRepository, WalletRepository walletRepository) {
        this.notificationService = notificationService;
        this.authorizationService = authorizationService;
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Transfer transfer(TransferDto transferDto) {

        Wallet sender = walletRepository.findById(transferDto.payer()).orElseThrow(() -> new ResourceNotFoundException("Wallet not found. id: "+ transferDto.payer()));
        Wallet receiver = walletRepository.findById(transferDto.payee()).orElseThrow(() -> new ResourceNotFoundException("Wallet not found. id: "+ transferDto.payee()));

        validateTransfer(transferDto, sender);

        sender.debit(transferDto.value());
        receiver.credit(transferDto.value());

        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setValue(transferDto.value());

        walletRepository.save(sender);
        walletRepository.save(receiver);
        Transfer transferResult = transferRepository.save(transfer);

        CompletableFuture.runAsync(() -> notificationService.sendNotification(transferResult));

        return transferResult;
    }

    private void validateTransfer(TransferDto transferDto, Wallet sender) {

        if (!sender.isTransferAllowedForWalletType()) {
            throw new TransferNotAllowedForWalletException("This wallet type is not allowed to transfer.");
        }

        if (!sender.hasSufficientBalance(transferDto.value())) {
            throw new InsufficientBalanceException("Insufficient balance. You cannot transfer a value bigger than your current balance.");
        }

        if (!authorizationService.isAuthorized(transferDto)) {
            throw new TransferNotAuthorizedException("Authorized service not authorized this transfer.");
        }

    }
}
