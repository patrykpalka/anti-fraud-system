package antifraud.service;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.utils.EntityUtils;
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

    @Test
    void addEntity_successfulAddIP() {
        // Arrange
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp("192.168.1.1");

        Function<SuspiciousIpRequestDTO, SuspiciousIp> toEntity = dto -> new SuspiciousIp(dto.getIp());
        Function<String, Optional<SuspiciousIp>> findEntityByField = ip -> Optional.empty();
        Consumer<SuspiciousIp> saveEntity = ip -> {};

        // Act
        ResponseEntity<SuspiciousIp> response = EntityUtils.addEntity(
                requestDTO,
                toEntity,
                findEntityByField,
                saveEntity,
                "IP address"
        );

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("192.168.1.1", response.getBody().getIp());
    }

    @Test
    void addEntity_conflictWhenEntityAlreadyExists() {
        // Arrange
        StolenCardRequestDTO requestDTO = new StolenCardRequestDTO();
        requestDTO.setNumber("1234567890123456");

        Function<StolenCardRequestDTO, StolenCard> toEntity = dto -> new StolenCard(dto.getNumber());
        Function<String, Optional<StolenCard>> findEntityByField =
                number -> Optional.of(new StolenCard(number));
        Consumer<StolenCard> saveEntity = card -> {};

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                EntityUtils.addEntity(
                        requestDTO,
                        toEntity,
                        findEntityByField,
                        saveEntity,
                        "card number"
                )
        );

        assertEquals("This card number is already in use", exception.getMessage());
    }

    @Test
    void addEntity_unsupportedDTOType() {
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

        assertEquals("Unsupported DTO type", exception.getMessage());
    }

    @Test
    void removeEntity_successfulRemoval() {
        // Arrange
        String field = "192.168.1.1";
        SuspiciousIp mockEntity = new SuspiciousIp(field);

        Function<String, Optional<SuspiciousIp>> findEntityByField =
                ip -> Optional.of(mockEntity);
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response =
                EntityUtils.removeEntity(
                        field,
                        findEntityByField,
                        deleteEntity,
                        "IP address"
                );

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(deleteEntity).accept(mockEntity);
    }

    @Test
    void removeEntity_notFoundThrowsException() {
        // Arrange
        String field = "192.168.1.1";

        Function<String, Optional<SuspiciousIp>> findEntityByField =
                ip -> Optional.empty();
        Consumer<SuspiciousIp> deleteEntity = mock(Consumer.class);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                EntityUtils.removeEntity(
                        field,
                        findEntityByField,
                        deleteEntity,
                        "IP address"
                )
        );

        assertEquals("The specified IP address (192.168.1.1) was not found.", exception.getMessage());
        verify(deleteEntity, never()).accept(any());
    }
}