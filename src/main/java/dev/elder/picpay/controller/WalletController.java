package dev.elder.picpay.controller;

import dev.elder.picpay.dto.request.WalletCreate;
import dev.elder.picpay.entity.Wallet;
import dev.elder.picpay.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallets")
    public ResponseEntity<Wallet> createWallet(@RequestBody @Valid WalletCreate dto) {

        Wallet wallet = walletService.createWallet(dto);

        return ResponseEntity.ok().body(wallet);
    }
}
