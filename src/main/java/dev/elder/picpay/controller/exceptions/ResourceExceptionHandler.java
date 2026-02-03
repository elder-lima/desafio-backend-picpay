package dev.elder.picpay.controller.exceptions;

import dev.elder.picpay.service.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

// Tratador Global de exceções, qualquer exceção lançada em qualquer controller da aplicação pode ser interceptada aqui.
@RestControllerAdvice
public class ResourceExceptionHandler {

    // Recurso não encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Recurso não encontrado."; // Mensagem padrão para o cliente
        HttpStatus status = HttpStatus.NOT_FOUND; // Status HTTP 404
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI()); // estrutura do erro
        return ResponseEntity.status(status).body(err);
    }


    // Tratando ConflictException (409)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<StandardError> conflict(
            ConflictException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT; // Retorna 409, semanticamente correto para duplicidade.

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Conflict",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    // Tratando erros de validação (@Valid) – 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Validation error");
        err.setMessage("Invalid fields");
        err.setPath(request.getRequestURI());

        // Percorre todos os campos inválidos, captura e adiciona na resposta de erro, no campo errors[]
        for (FieldError f : e.getBindingResult() //relatório completo da validação
                .getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // Erro de não autorização no serviço externo
    @ExceptionHandler(ExternalServiceForbiddenException.class)
    public ResponseEntity<StandardError> handleExternal(ExternalServiceForbiddenException e, HttpServletRequest request) {

        String error = "Bad Gateway";
        HttpStatus status = HttpStatus.valueOf(502);
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(NotificationClientException.class)
    public ResponseEntity<StandardError> handleNotificationClientException(
            NotificationClientException e, HttpServletRequest request) {



        StandardError error = new StandardError(
                Instant.now(),
                e.getStatusCode(),
                "Erro de Notificação de Cliente",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getStatusCode())
                .body(error);
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<StandardError> handleNotificationException(
            NotificationException e, HttpServletRequest request) {

        StandardError error = new StandardError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro no serviço de Notificação",
                e.getMessage(),
                request.getRequestURI()

        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(TransferNotAllowedForWalletException.class)
    public ResponseEntity<StandardError> transferNotAllowedForWallet(TransferNotAllowedForWalletException e, HttpServletRequest request) {

        String error = "Transfer not allowed to this wallet";
        HttpStatus status = HttpStatus.valueOf(422);
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<StandardError> insufficientBalance(InsufficientBalanceException e, HttpServletRequest request) {

        String error = "Insufficient balance";
        HttpStatus status = HttpStatus.valueOf(422);
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(TransferNotAuthorizedException.class)
    public ResponseEntity<StandardError> transferNotAuthorized(TransferNotAuthorizedException e, HttpServletRequest request) {

        String error = "Transfer not Authorized";
        HttpStatus status = HttpStatus.valueOf(422);
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }



}
