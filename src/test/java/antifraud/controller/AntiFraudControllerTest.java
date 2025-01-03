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
        validIp = "192.168.1.1";
        validCardNumber = "1234567890123456";
    }

    // Suspicious IP Tests

    @Test
    void shouldAddSuspiciousIpSuccessfully() {
        SuspiciousIpRequestDTO request = createSuspiciousIpRequest(validIp);
        SuspiciousIp expectedIp = new SuspiciousIp(validIp);
        when(antiFraudService.addSuspiciousIp(request)).thenReturn(ResponseEntity.ok(expectedIp));

        ResponseEntity<SuspiciousIp> response = antiFraudController.addSuspiciousIp(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedIp, response.getBody());
        verify(antiFraudService).addSuspiciousIp(request);
    }

    @Test
    void shouldRetrieveSuspiciousIPs() {
        List<SuspiciousIp> expectedIps = Collections.singletonList(new SuspiciousIp(validIp));
        when(antiFraudService.getSuspiciousIps()).thenReturn(ResponseEntity.ok(expectedIps));

        ResponseEntity<List<SuspiciousIp>> response = antiFraudController.getSuspiciousIps();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedIps, response.getBody());
        verify(antiFraudService).getSuspiciousIps();
    }

    @Test
    void shouldRemoveSuspiciousIpSuccessfully() {
        AntiFraudDeletionResponseDTO<SuspiciousIp> expectedResponse =
                new AntiFraudDeletionResponseDTO<>(new SuspiciousIp(validIp));
        when(antiFraudService.removeSuspiciousIp(validIp)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response =
                antiFraudController.removeSuspiciousIp(validIp);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(antiFraudService).removeSuspiciousIp(validIp);
    }

    // Stolen Card Tests

    @Test
    void shouldAddStolenCardSuccessfully() {
        StolenCardRequestDTO request = createStolenCardRequest(validCardNumber);
        StolenCard expectedCard = new StolenCard(validCardNumber);
        when(antiFraudService.addStolenCard(request)).thenReturn(ResponseEntity.ok(expectedCard));

        ResponseEntity<StolenCard> response = antiFraudController.addStolenCard(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCard, response.getBody());
        verify(antiFraudService).addStolenCard(request);
    }

    @Test
    void shouldRetrieveStolenCards() {
        List<StolenCard> expectedCards = Collections.singletonList(new StolenCard(validCardNumber));
        when(antiFraudService.getStolenCards()).thenReturn(ResponseEntity.ok(expectedCards));

        ResponseEntity<List<StolenCard>> response = antiFraudController.getStolenCards();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());
        verify(antiFraudService).getStolenCards();
    }

    @Test
    void shouldRemoveStolenCardSuccessfully() {
        AntiFraudDeletionResponseDTO<StolenCard> expectedResponse =
                new AntiFraudDeletionResponseDTO<>(new StolenCard(validCardNumber));
        when(antiFraudService.removeStolenCard(validCardNumber)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> response =
                antiFraudController.removeStolenCard(validCardNumber);

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
