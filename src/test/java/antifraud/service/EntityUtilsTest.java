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

    @BeforeEach
    void setUp() {
        toSuspiciousIpEntity = dto -> new SuspiciousIp(dto.getIp());
        findSuspiciousIpByField = ip -> Optional.empty();
        saveSuspiciousIp = ip -> {};
    }

    @Test
    @DisplayName("Should successfully add a new IP address when not already present")
    void shouldSuccessfullyAddIPWhenNotPresent() {
        // Arrange
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp("192.168.1.1");

        // Act
        ResponseEntity<SuspiciousIp> response = EntityUtils.addEntity(
                requestDTO,
                toSuspiciousIpEntity,
                findSuspiciousIpByField,
                saveSuspiciousIp,
                "IP address"
        );

        // Assert
        assertNotNull(response, "The response should not be null.");
        assertEquals(200, response.getStatusCode().value(), "The response status code should be 200 OK.");
        assertNotNull(response.getBody(), "The response body should not be null.");
        assertEquals("192.168.1.1", response.getBody().getIp(), "The IP in the response should match the input.");
    }

    @Test
    @DisplayName("Should throw ConflictException when the IP address already exists")
    void shouldThrowConflictExceptionWhenIPAlreadyExists() {
        // Arrange
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp("192.168.1.1");

        findSuspiciousIpByField = ip -> Optional.of(new SuspiciousIp(ip));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                EntityUtils.addEntity(
                        requestDTO,
                        toSuspiciousIpEntity,
                        findSuspiciousIpByField,
                        saveSuspiciousIp,
                        "IP address"
                )
        );

        assertEquals("This IP address is already in use", exception.getMessage(), "The exception message should indicate a conflict.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given an unsupported DTO type")
    void shouldThrowIllegalArgumentExceptionWhenUnsupportedDTOType() {
        // Arrange
        Object unsupportedDTO = new Object();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                EntityUtils.addEntity(
                        unsupportedDTO,
                        dto -> null,
                        field -> Optional.empty(),
                        entity -> {},
                        "unsupported type"
                )
        );

        assertEquals("Unsupported DTO type", exception.getMessage(), "The exception message should indicate an unsupported DTO type.");
    }

    @Test
    @DisplayName("Should successfully remove an IP address when found")
    void shouldSuccessfullyRemoveIPWhenFound() {
        // Arrange
        String field = "192.168.1.1";
        SuspiciousIp mockEntity = new SuspiciousIp(field);

        findSuspiciousIpByField = ip -> Optional.of(mockEntity);
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response = EntityUtils.removeEntity(
                field,
                findSuspiciousIpByField,
                deleteEntity,
                "IP address"
        );

        // Assert
        assertNotNull(response, "The response should not be null.");
        assertEquals(200, response.getStatusCode().value(), "The response status code should be 200 OK.");
        assertNotNull(response.getBody(), "The response body should not be null.");
        verify(deleteEntity).accept(mockEntity);
    }

    @Test
    @DisplayName("Should throw NotFoundException when the IP address is not found")
    void shouldThrowNotFoundExceptionWhenIPNotFound() {
        // Arrange
        String field = "192.168.1.1";

        findSuspiciousIpByField = ip -> Optional.empty();
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                EntityUtils.removeEntity(
                        field,
                        findSuspiciousIpByField,
                        deleteEntity,
                        "IP address"
                )
        );

        assertEquals("The specified IP address (192.168.1.1) was not found.", exception.getMessage(), "The exception message should indicate the IP was not found.");
        verify(deleteEntity, never()).accept(any());
    }
}
