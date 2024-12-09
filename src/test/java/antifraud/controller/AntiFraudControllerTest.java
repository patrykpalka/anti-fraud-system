package antifraud.controller;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.service.AntiFraudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AntiFraudControllerTest {

    @Mock
    private AntiFraudService antiFraudService;

    @InjectMocks
    private AntiFraudController antiFraudController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSuspiciousIp_ValidInput_ReturnsResponse() {
        SuspiciousIpRequestDTO request = new SuspiciousIpRequestDTO();
        request.setIp("192.168.1.1");
        SuspiciousIp expectedIp = new SuspiciousIp("192.168.1.1");
        when(antiFraudService.addSuspiciousIp(request)).thenReturn(ResponseEntity.ok(expectedIp));

        ResponseEntity<SuspiciousIp> response = antiFraudController.addSuspiciousIp(request);

        assertEquals(expectedIp, response.getBody());
        verify(antiFraudService).addSuspiciousIp(request);
    }

    @Test
    void getSuspiciousIps_ReturnsList() {
        List<SuspiciousIp> expectedIps = Collections.singletonList(new SuspiciousIp("192.168.1.1"));
        when(antiFraudService.getSuspiciousIps()).thenReturn(ResponseEntity.ok(expectedIps));

        ResponseEntity<List<SuspiciousIp>> response = antiFraudController.getSuspiciousIps();

        assertEquals(expectedIps, response.getBody());
        verify(antiFraudService).getSuspiciousIps();
    }

    @Test
    void removeSuspiciousIp_ValidIp_ReturnsResponse() {
        String ip = "192.168.1.1";
        AntiFraudDeletionResponseDTO<SuspiciousIp> expectedResponse = new AntiFraudDeletionResponseDTO<>(new SuspiciousIp(ip));
        when(antiFraudService.removeSuspiciousIp(ip)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response = antiFraudController.removeSuspiciousIp(ip);

        assertEquals(expectedResponse, response.getBody());
        verify(antiFraudService).removeSuspiciousIp(ip);
    }

    @Test
    void addStolenCard_ValidInput_ReturnsResponse() {
        StolenCardRequestDTO request = new StolenCardRequestDTO();
        request.setNumber("1234567890123456");
        StolenCard expectedCard = new StolenCard("1234567890123456");
        when(antiFraudService.addStolenCard(request)).thenReturn(ResponseEntity.ok(expectedCard));

        ResponseEntity<StolenCard> response = antiFraudController.addStolenCard(request);

        assertEquals(expectedCard, response.getBody());
        verify(antiFraudService).addStolenCard(request);
    }

    @Test
    void getStolenCards_ReturnsList() {
        List<StolenCard> expectedCards = Collections.singletonList(new StolenCard("1234567890123456"));
        when(antiFraudService.getStolenCards()).thenReturn(ResponseEntity.ok(expectedCards));

        ResponseEntity<List<StolenCard>> response = antiFraudController.getStolenCards();

        assertEquals(expectedCards, response.getBody());
        verify(antiFraudService).getStolenCards();
    }

    @Test
    void removeStolenCard_ValidNumber_ReturnsResponse() {
        String number = "1234567890123456";
        AntiFraudDeletionResponseDTO<StolenCard> expectedResponse = new AntiFraudDeletionResponseDTO<>(new StolenCard(number));
        when(antiFraudService.removeStolenCard(number)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> response = antiFraudController.removeStolenCard(number);

        assertEquals(expectedResponse, response.getBody());
        verify(antiFraudService).removeStolenCard(number);
    }
}
