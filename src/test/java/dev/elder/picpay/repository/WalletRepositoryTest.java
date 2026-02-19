package dev.elder.picpay.repository;


import dev.elder.picpay.dto.request.WalletCreate;
import dev.elder.picpay.entity.Wallet;
import dev.elder.picpay.entity.enums.WalletType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class WalletRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    WalletRepository walletRepository;

    @Test
    @DisplayName("Retornar Wallet com sucesso do banco de dados")
    void findByCpfCnpjOrEmailCase1() {

        String cpf = "74114774147";
        String email = "elder@gmail.com";
        WalletCreate dto = new WalletCreate("Elder", cpf, email, "elder", WalletType.USER);
        this.createWallet(dto);

        Optional<Wallet> result =  this.walletRepository.findByCpfCnpjOrEmail(cpf, email);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Não Retornar Wallet quando ela não existe")
    void findByCpfCnpjOrEmailCase2() {

        String cpf = "74114774147";
        String email = "elder@gmail.com";

        Optional<Wallet> result =  this.walletRepository.findByCpfCnpjOrEmail(cpf, email);

        assertThat(result.isEmpty()).isTrue();
    }

    private Wallet createWallet(WalletCreate dto) {

        Wallet newWallet = new Wallet();
        newWallet.setFullName(dto.fullName());
        newWallet.setCpfCnpj(dto.cpfCnpj());
        newWallet.setEmail(dto.email());
        newWallet.setPassword(dto.password());
        newWallet.setWalletType(dto.walletType());

        this.entityManager.persist(newWallet);
        return newWallet;
    }

}