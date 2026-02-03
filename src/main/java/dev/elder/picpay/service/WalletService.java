package dev.elder.picpay.service;

import dev.elder.picpay.dto.request.WalletCreate;
import dev.elder.picpay.entity.Wallet;
import dev.elder.picpay.repository.WalletRepository;
import dev.elder.picpay.service.exceptions.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Wallet createWallet(WalletCreate dto) {

        var walletDb = walletRepository.findByCpfCnpjOrEmail(dto.cpfCnpj(), dto.email());
        if (walletDb.isPresent()) {
            throw new ConflictException("CpfCnpj or Email already exists");
        }

        Wallet wallet = new Wallet();
        wallet.setFullName(dto.fullName());
        wallet.setCpfCnpj(dto.cpfCnpj());
        wallet.setEmail(dto.email());
        wallet.setPassword(dto.password());
        wallet.setWalletType(dto.walletType());
        walletRepository.save(wallet);
        return wallet;
    }
}
