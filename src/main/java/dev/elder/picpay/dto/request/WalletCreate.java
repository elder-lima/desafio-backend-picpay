package dev.elder.picpay.dto.request;

import dev.elder.picpay.entity.enums.WalletType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WalletCreate(

        @NotBlank(message = "Full name is required")
        String fullName,
        @NotBlank(message = "CPF or Cnpj is required")
        String cpfCnpj,
        @Email(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        @NotNull(message = "Wallet Type is required")
        WalletType walletType

) {}
