package antifraud.controller;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.AntiFraudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AntiFraudControllerTest {

    @Mock
    private AntiFraudService antiFraudService;

    @InjectMocks
    private AntiFraudController antiFraudController;

    private String validIp;
    private String validCardNumber;

    @BeforeEach
    void setUp() {
        // Common setup for all tests
        validIp = "192.168.1.1";
        validCardNumber = "1234567890123456";
    }

    // Suspicious IP Tests

    @Test
    @DisplayName("Should successfully add a suspicious IP")
    void shouldAddSuspiciousIpSuccessfully() {
        // Arrange
        SuspiciousIpRequestDTO request = createSuspiciousIpRequest(validIp);
        SuspiciousIp expectedIp = new SuspiciousIp(validIp);
        when(antiFraudService.addSuspiciousIp(request)).thenReturn(ResponseEntity.ok(expectedIp));

        // Act
        ResponseEntity<SuspiciousIp> response = antiFraudController.addSuspiciousIp(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedIp, response.getBody());
        verify(antiFraudService).addSuspiciousIp(request);
    }

    @Test
    @DisplayName("Should retrieve list of suspicious IPs")
    void shouldRetrieveSuspiciousIPs() {
        // Arrange
        List<SuspiciousIp> expectedIps = Collections.singletonList(new SuspiciousIp(validIp));
        when(antiFraudService.getSuspiciousIps()).thenReturn(ResponseEntity.ok(expectedIps));

        // Act
        ResponseEntity<List<SuspiciousIp>> response = antiFraudController.getSuspiciousIps();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedIps, response.getBody());
        verify(antiFraudService).getSuspiciousIps();
    }

    @Test
    @DisplayName("Should successfully remove a suspicious IP")
    void shouldRemoveSuspiciousIpSuccessfully() {
        // Arrange
        AntiFraudDeletionResponseDTO<SuspiciousIp> expectedResponse =
                new AntiFraudDeletionResponseDTO<>(new SuspiciousIp(validIp));
        when(antiFraudService.removeSuspiciousIp(validIp)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response =
                antiFraudController.removeSuspiciousIp(validIp);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(antiFraudService).removeSuspiciousIp(validIp);
    }

    @Test
    @DisplayName("Should successfully add a stolen card")
    void shouldAddStolenCardSuccessfully() {
        // Arrange
        StolenCardRequestDTO request = createStolenCardRequest(validCardNumber);
        StolenCard expectedCard = new StolenCard(validCardNumber);
        when(antiFraudService.addStolenCard(request)).thenReturn(ResponseEntity.ok(expectedCard));

        // Act
        ResponseEntity<StolenCard> response = antiFraudController.addStolenCard(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCard, response.getBody());
        verify(antiFraudService).addStolenCard(request);
    }

    @Test
    @DisplayName("Should retrieve list of stolen cards")
    void shouldRetrieveStolenCards() {
        // Arrange
        List<StolenCard> expectedCards = Collections.singletonList(new StolenCard(validCardNumber));
        when(antiFraudService.getStolenCards()).thenReturn(ResponseEntity.ok(expectedCards));

        // Act
        ResponseEntity<List<StolenCard>> response = antiFraudController.getStolenCards();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());
        verify(antiFraudService).getStolenCards();
    }

    @Test
    @DisplayName("Should successfully remove a stolen card")
    void shouldRemoveStolenCardSuccessfully() {
        // Arrange
        AntiFraudDeletionResponseDTO<StolenCard> expectedResponse =
                new AntiFraudDeletionResponseDTO<>(new StolenCard(validCardNumber));
        when(antiFraudService.removeStolenCard(validCardNumber)).thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> response =
                antiFraudController.removeStolenCard(validCardNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(antiFraudService).removeStolenCard(validCardNumber);
    }

    // Helper Methods

    private SuspiciousIpRequestDTO createSuspiciousIpRequest(String ip) {
        SuspiciousIpRequestDTO request = new SuspiciousIpRequestDTO();
        request.setIp(ip);
        return request;
    }

    private StolenCardRequestDTO createStolenCardRequest(String number) {
        StolenCardRequestDTO request = new StolenCardRequestDTO();
        request.setNumber(number);
        return request;
    }
}
