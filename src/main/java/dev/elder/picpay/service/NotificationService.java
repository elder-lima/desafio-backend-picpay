package dev.elder.picpay.service;

import dev.elder.picpay.client.NotificationClient;
import dev.elder.picpay.entity.Transfer;
import dev.elder.picpay.service.exceptions.NotificationClientException;
import dev.elder.picpay.service.exceptions.NotificationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void sendNotification(Transfer transfer) {

        try {
            var resp = notificationClient.sendNotification(transfer);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new NotificationClientException("Erro ao enviar notificação ", e.getStatusCode().value());
        } catch (RestClientException e) { // tratar qualquer erro de comunicação
            throw new NotificationException("Erro de comunicação com serviço de notificação", e);
        }
    }

}
