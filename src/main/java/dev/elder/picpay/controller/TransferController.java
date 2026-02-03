package dev.elder.picpay.controller;

import dev.elder.picpay.dto.request.TransferDto;
import dev.elder.picpay.entity.Transfer;
import dev.elder.picpay.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transfer> transfer(@RequestBody @Valid TransferDto dto) {

        var response = transferService.transfer(dto);
        return ResponseEntity.ok().body(response);
    }

}
