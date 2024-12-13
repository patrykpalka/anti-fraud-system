package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.SuspiciousIp;
import antifraud.service.utils.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityUtilsTest {

    private Function<SuspiciousIpRequestDTO, SuspiciousIp> toSuspiciousIpEntity;
    private Function<String, Optional<SuspiciousIp>> findSuspiciousIpByField;
    private Consumer<SuspiciousIp> saveSuspiciousIp;
    private Consumer<SuspiciousIp> mockEventPublisher;

    @BeforeEach
    void setUp() {
        toSuspiciousIpEntity = dto -> new SuspiciousIp(dto.getIp());
        findSuspiciousIpByField = ip -> Optional.empty();
        saveSuspiciousIp = ip -> {};
        mockEventPublisher = mock(Consumer.class);
    }

    @Test
    void shouldSuccessfullyAddIPWhenNotPresent() {
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp("192.168.1.1");

        ResponseEntity<SuspiciousIp> response = EntityUtils.addEntity(
                requestDTO,
                toSuspiciousIpEntity,
                findSuspiciousIpByField,
                saveSuspiciousIp,
                "IP address",
                mockEventPublisher
        );

        assertNotNull(response, "The response should not be null.");
        assertEquals(200, response.getStatusCode().value(), "The response status code should be 200 OK.");
        assertNotNull(response.getBody(), "The response body should not be null.");
        assertEquals("192.168.1.1", response.getBody().getIp(), "The IP in the response should match the input.");
        verify(mockEventPublisher, times(1)).accept(response.getBody());
    }

    @Test
    void shouldThrowConflictExceptionWhenIPAlreadyExists() {
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp("192.168.1.1");

        findSuspiciousIpByField = ip -> Optional.of(new SuspiciousIp(ip));

        ConflictException exception = assertThrows(ConflictException.class, () ->
                EntityUtils.addEntity(
                        requestDTO,
                        toSuspiciousIpEntity,
                        findSuspiciousIpByField,
                        saveSuspiciousIp,
                        "IP address",
                        mockEventPublisher
                )
        );

        assertEquals("This IP address is already in use", exception.getMessage(), "The exception message should indicate a conflict.");
        verify(mockEventPublisher, never()).accept(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUnsupportedDTOType() {
        Object unsupportedDTO = new Object();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                EntityUtils.addEntity(
                        unsupportedDTO,
                        dto -> null,
                        field -> Optional.empty(),
                        entity -> {},
                        "unsupported type",
                        mockEventPublisher
                )
        );

        assertEquals("Unsupported DTO type", exception.getMessage(), "The exception message should indicate an unsupported DTO type.");
        verify(mockEventPublisher, never()).accept(any());
    }

    @Test
    void shouldSuccessfullyRemoveIPWhenFound() {
        String field = "192.168.1.1";
        SuspiciousIp mockEntity = new SuspiciousIp(field);

        findSuspiciousIpByField = ip -> Optional.of(mockEntity);
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response = EntityUtils.removeEntity(
                field,
                findSuspiciousIpByField,
                deleteEntity,
                "IP address",
                mockEventPublisher
        );

        assertNotNull(response, "The response should not be null.");
        assertEquals(200, response.getStatusCode().value(), "The response status code should be 200 OK.");
        assertNotNull(response.getBody(), "The response body should not be null.");
        verify(deleteEntity, times(1)).accept(mockEntity);
        verify(mockEventPublisher, times(1)).accept(mockEntity);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenIPNotFound() {
        String field = "192.168.1.1";

        findSuspiciousIpByField = ip -> Optional.empty();
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                EntityUtils.removeEntity(
                        field,
                        findSuspiciousIpByField,
                        deleteEntity,
                        "IP address",
                        mockEventPublisher
                )
        );

        assertEquals("The specified IP address (192.168.1.1) was not found.", exception.getMessage(), "The exception message should indicate the IP was not found.");
        verify(deleteEntity, never()).accept(any());
        verify(mockEventPublisher, never()).accept(any());
    }
}
