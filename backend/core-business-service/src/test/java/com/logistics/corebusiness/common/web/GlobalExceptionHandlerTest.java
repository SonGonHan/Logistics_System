package com.logistics.corebusiness.common.web;

import com.logistics.corebusiness.waybill.application.exception.DraftAccessDeniedException;
import com.logistics.corebusiness.waybill.application.exception.DraftInvalidStatusException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.DraftValidationException;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - тестирование обработки исключений")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Должен обработать DraftNotFoundException и вернуть 404")
    void shouldHandleDraftNotFoundException() {
        // Given
        var exception = DraftNotFoundException.byId(123L);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDraftNotFound(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("DRAFT_NOT_FOUND");
        assertThat(response.getBody().get("message")).asString().contains("Draft not found with id: 123");
    }

    @Test
    @DisplayName("Должен обработать DraftAccessDeniedException и вернуть 403")
    void shouldHandleDraftAccessDeniedException() {
        // Given
        var exception = DraftAccessDeniedException.forOperation("update");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDraftAccessDenied(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("ACCESS_DENIED");
        assertThat(response.getBody().get("message")).asString().contains("you can only update your own drafts");
    }

    @Test
    @DisplayName("Должен обработать DraftInvalidStatusException и вернуть 409")
    void shouldHandleDraftInvalidStatusException() {
        // Given
        var exception = DraftInvalidStatusException.forOperation("update", DraftStatus.CONFIRMED);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDraftInvalidStatus(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INVALID_DRAFT_STATUS");
        assertThat(response.getBody().get("message")).isNotNull();
    }

    @Test
    @DisplayName("Должен обработать DraftValidationException и вернуть 400")
    void shouldHandleDraftValidationException() {
        // Given
        var exception = new DraftValidationException("Either id or barcode must be provided");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDraftValidation(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().get("message")).isEqualTo("Either id or barcode must be provided");
    }

    @Test
    @DisplayName("Должен обработать MethodArgumentNotValidException и вернуть 400 с деталями")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        var fieldError1 = new FieldError("createDraftRequest", "recipientAddress", "Адрес доставки обязателен");
        var fieldError2 = new FieldError("createDraftRequest", "weightDeclared", "Вес должен быть больше 0");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        var exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("VALIDATION_FAILED");

        @SuppressWarnings("unchecked")
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields).isNotNull();
        assertThat(fields).hasSize(2);
        assertThat(fields.get("recipientAddress")).isEqualTo("Адрес доставки обязателен");
        assertThat(fields.get("weightDeclared")).isEqualTo("Вес должен быть больше 0");
    }

    @Test
    @DisplayName("Должен обработать DataIntegrityViolationException и вернуть 409 с оригинальным сообщением")
    void shouldHandleDataIntegrityViolationException() {
        // Given
        String exceptionMessage = "Duplicate entry 'DRF-260209-123456' for key 'barcode'";
        var exception = new DataIntegrityViolationException(exceptionMessage);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("CONFLICT");
        assertThat(response.getBody().get("message")).isEqualTo(exceptionMessage);
    }

    @Test
    @DisplayName("Должен обработать DataIntegrityViolationException с другим сообщением")
    void shouldHandleDataIntegrityViolationExceptionWithCustomMessage() {
        // Given
        String exceptionMessage = "Some other constraint violation";
        var exception = new DataIntegrityViolationException(exceptionMessage);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("CONFLICT");
        assertThat(response.getBody().get("message")).isEqualTo(exceptionMessage);
    }

    @Test
    @DisplayName("Должен обработать DataIntegrityViolationException с null сообщением")
    void shouldHandleDataIntegrityViolationExceptionWithNullMessage() {
        // Given
        var exception = new DataIntegrityViolationException(null);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("CONFLICT");
        assertThat(response.getBody().get("message")).isNull();
    }

    @Test
    @DisplayName("Должен обработать общее Exception и вернуть 500 с оригинальным сообщением")
    void shouldHandleGenericException() {
        // Given
        String exceptionMessage = "Unexpected error occurred";
        var exception = new RuntimeException(exceptionMessage);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().get("message")).isEqualTo(exceptionMessage);
    }

    @Test
    @DisplayName("Должен обработать NullPointerException и вернуть 500")
    void shouldHandleNullPointerException() {
        // Given
        String exceptionMessage = "null pointer";
        var exception = new NullPointerException(exceptionMessage);

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().get("message")).isEqualTo(exceptionMessage);
    }
}
