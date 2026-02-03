package dev.elder.picpay.service;

import dev.elder.picpay.client.AuthorizationClient;
import dev.elder.picpay.dto.request.TransferDto;
import dev.elder.picpay.entity.Transfer;
import dev.elder.picpay.service.exceptions.ExternalServiceForbiddenException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final AuthorizationClient authorizationClient;

    public AuthorizationService(AuthorizationClient authorizationClient) {
        this.authorizationClient = authorizationClient;
    }

    public boolean isAuthorized(TransferDto transfer) {

        var response = authorizationClient.isAuthorized();

        if (response.getStatusCode().isError()) {
            throw new ExternalServiceForbiddenException("Acesso negado pelo serviço externo de autorização");
        }

        return response.getBody().data().authorization();
    }
}
