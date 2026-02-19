    package dev.elder.picpay.service;

    import dev.elder.picpay.dto.request.TransferDto;
    import dev.elder.picpay.dto.request.WalletCreate;
    import dev.elder.picpay.entity.Transfer;
    import dev.elder.picpay.entity.Wallet;
    import dev.elder.picpay.entity.enums.WalletType;
    import dev.elder.picpay.repository.TransferRepository;
    import dev.elder.picpay.repository.WalletRepository;
    import dev.elder.picpay.service.exceptions.InsufficientBalanceException;
    import dev.elder.picpay.service.exceptions.ResourceNotFoundException;
    import dev.elder.picpay.service.exceptions.TransferNotAllowedForWalletException;
    import dev.elder.picpay.service.exceptions.TransferNotAuthorizedException;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.math.BigDecimal;
    import java.util.Optional;
    import java.util.Random;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    class TransferServiceTest {

        @Mock
        NotificationService notificationService;

        @Mock
        AuthorizationService authorizationService;

        @Mock
        TransferRepository transferRepository;

        @Mock
        WalletRepository walletRepository;

        @InjectMocks
        TransferService transferService;

        @Test
        @DisplayName("Transferência com Sucesso")
        void transferCase1() {

            // ARRANGE
            WalletCreate senderDto = new WalletCreate("elder", "12345678910", "elder@gmail.com", "elder", WalletType.USER);
            WalletCreate receiverDto = new WalletCreate("joao", "14714714714", "joao@gmail.com", "joao", WalletType.MERCHANT);
            Wallet sender = createWallet(senderDto);
            sender.setBalance(BigDecimal.valueOf(200));
            Wallet receiver = createWallet(receiverDto);
            TransferDto transferDto = new TransferDto(BigDecimal.valueOf(50), sender.getId(), receiver.getId());

            when(walletRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
            when(walletRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
            when(authorizationService.isAuthorized(transferDto)).thenReturn(true);
            when(transferRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            // ACT
            Transfer result = transferService.transfer(transferDto);

            // ASSERT
            assertEquals(BigDecimal.valueOf(50), result.getValue());
            assertEquals(BigDecimal.valueOf(150), sender.getBalance());
            assertEquals(BigDecimal.valueOf(50), receiver.getBalance());

            verify(walletRepository).save(sender);
            verify(walletRepository).save(receiver);
            verify(transferRepository).save(any(Transfer.class));

        }

        @Test
        @DisplayName("Wallet não encontrada")
        void transferCase2() {

            // ARRANGE
            TransferDto transferDto = new TransferDto(BigDecimal.valueOf(50), 1L, 2L);

            when(walletRepository.findById(1L)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class, () -> transferService.transfer(transferDto));

        }

        @Test
        @DisplayName("Saldo Insuficiente")
        void transferCase3() {
            // ARRANGE
            WalletCreate senderDto = new WalletCreate("elder", "12345678910", "elder@gmail.com", "elder", WalletType.USER);
            WalletCreate receiverDto = new WalletCreate("joao", "14714714714", "joao@gmail.com", "joao", WalletType.MERCHANT);
            Wallet sender = createWallet(senderDto);
            sender.setBalance(BigDecimal.valueOf(200));
            Wallet receiver = createWallet(receiverDto);
            TransferDto transferDto = new TransferDto(BigDecimal.valueOf(500), sender.getId(), receiver.getId());

            when(walletRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
            when(walletRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

            // ACT & ASSERT
            assertThrows(InsufficientBalanceException.class, () -> transferService.transfer(transferDto));

        }

        @Test
        @DisplayName("Não Autorizado")
        void transferCase4() {
            // ARRANGE
            WalletCreate senderDto = new WalletCreate("elder", "12345678910", "elder@gmail.com", "elder", WalletType.USER);
            WalletCreate receiverDto = new WalletCreate("joao", "14714714714", "joao@gmail.com", "joao", WalletType.MERCHANT);
            Wallet sender = createWallet(senderDto);
            sender.setBalance(BigDecimal.valueOf(200));
            Wallet receiver = createWallet(receiverDto);
            TransferDto transferDto = new TransferDto(BigDecimal.valueOf(50), sender.getId(), receiver.getId());

            when(walletRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
            when(walletRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
            when(authorizationService.isAuthorized(transferDto)).thenReturn(false);

            assertThrows(TransferNotAuthorizedException.class,
                    () -> transferService.transfer(transferDto));

        }

        @Test
        @DisplayName("Wallet Type Invalido para transferencia")
        void tranferCase5() {

            // ARRANGE
            WalletCreate senderDto = new WalletCreate("elder", "12345678910", "elder@gmail.com", "elder", WalletType.MERCHANT);
            WalletCreate receiverDto = new WalletCreate("joao", "14714714714", "joao@gmail.com", "joao", WalletType.MERCHANT);
            Wallet sender = createWallet(senderDto);
            sender.setBalance(BigDecimal.valueOf(200));
            Wallet receiver = createWallet(receiverDto);
            TransferDto transferDto = new TransferDto(BigDecimal.valueOf(50), sender.getId(), receiver.getId());

            when(walletRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
            when(walletRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

            assertThrows(TransferNotAllowedForWalletException.class, () -> transferService.transfer(transferDto));

        }

        @Test
        @DisplayName("Receiver não encontrado")
        void transferCase6() {

            TransferDto dto = new TransferDto(
                    BigDecimal.valueOf(50), 1L, 2L
            );

            Wallet sender = new Wallet();
            sender.setId(1L);
            sender.setWalletType(WalletType.USER);
            sender.setBalance(BigDecimal.valueOf(200));

            when(walletRepository.findById(1L))
                    .thenReturn(Optional.of(sender));
            when(walletRepository.findById(2L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> transferService.transfer(dto));
        }

        private Wallet createWallet(WalletCreate dto) {
            Wallet newWallet = new Wallet();
            newWallet.setId(Math.abs(new Random().nextLong()));
            newWallet.setFullName(dto.fullName());
            newWallet.setCpfCnpj(dto.cpfCnpj());
            newWallet.setEmail(dto.email());
            newWallet.setPassword(dto.password());
            newWallet.setWalletType(dto.walletType());

            return newWallet;
        }
    }