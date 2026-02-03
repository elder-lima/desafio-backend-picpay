package dev.elder.picpay.client.dto;

public record AuthorizationResponse(

        String status,
        AuthorizationData data

) {}
