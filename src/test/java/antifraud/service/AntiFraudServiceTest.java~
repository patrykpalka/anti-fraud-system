package antifraud.service;

import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AntiFraudServiceTest {

    @Mock
    private SuspiciousIpRepo suspiciousIpRepo;

    @Mock
    private StolenCardRepo stolenCardRepo;

    @InjectMocks
    private AntiFraudService antiFraudService;

    @Test
    void testAddSuspiciousIpSuccess() {
        // Given
        String ip = "192.168.1.1";
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp(ip);
        SuspiciousIp suspiciousIp = new SuspiciousIp(ip);
        when(suspiciousIpRepo.findByIp(ip)).thenReturn(Optional.empty());
        when(suspiciousIpRepo.save(any(SuspiciousIp.class))).thenReturn(suspiciousIp);

        // When
        ResponseEntity<?> response = antiFraudService.addSuspiciousIp(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(ip, ((SuspiciousIp) response.getBody()).getIp());
        verify(suspiciousIpRepo, times(1)).save(any(SuspiciousIp.class));
    }

    @Test
    void testAddSuspiciousIpConflict() {
        // Given
        String ip = "192.168.1.1";
        SuspiciousIpRequestDTO requestDTO = new SuspiciousIpRequestDTO();
        requestDTO.setIp(ip);
        when(suspiciousIpRepo.findByIp(ip)).thenReturn(Optional.of(new SuspiciousIp(ip)));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> antiFraudService.addSuspiciousIp(requestDTO));
        assertEquals("This IP address is already in use", exception.getMessage());
        verify(suspiciousIpRepo, times(0)).save(any(SuspiciousIp.class));
    }

    @Test
    void testRemoveSuspiciousIpSuccess() {
        // Given
        String ip = "192.168.1.1";
        SuspiciousIp suspiciousIp = new SuspiciousIp(ip);
        when(suspiciousIpRepo.findByIp(ip)).thenReturn(Optional.of(suspiciousIp));

        // When
        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response = antiFraudService.removeSuspiciousIp(ip);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        // Change the assertion to check the content of the body, not the status
        assertTrue(response.getBody().getStatus().contains(ip));
        verify(suspiciousIpRepo, times(1)).delete(suspiciousIp);
    }

    @Test
    void testRemoveSuspiciousIpNotFound() {
        // Given
        String ip = "192.168.1.1";
        when(suspiciousIpRepo.findByIp(ip)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> antiFraudService.removeSuspiciousIp(ip));
        assertEquals("The specified IP address (192.168.1.1) was not found.", exception.getMessage());
        verify(suspiciousIpRepo, times(0)).delete(any(SuspiciousIp.class));
    }

    @Test
    void testAddStolenCardSuccess() {
        // Given
        String cardNumber = "1234567812345678";
        StolenCardRequestDTO requestDTO = new StolenCardRequestDTO();
        requestDTO.setNumber(cardNumber);
        StolenCard stolenCard = new StolenCard(cardNumber);
        when(stolenCardRepo.findByNumber(cardNumber)).thenReturn(Optional.empty());
        when(stolenCardRepo.save(any(StolenCard.class))).thenReturn(stolenCard);

        // When
        ResponseEntity<?> response = antiFraudService.addStolenCard(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(cardNumber, ((StolenCard) response.getBody()).getNumber());
        verify(stolenCardRepo, times(1)).save(any(StolenCard.class));
    }

    @Test
    void testAddStolenCardConflict() {
        // Given
        String cardNumber = "1234567812345678";
        StolenCardRequestDTO requestDTO = new StolenCardRequestDTO();
        requestDTO.setNumber(cardNumber);
        when(stolenCardRepo.findByNumber(cardNumber)).thenReturn(Optional.of(new StolenCard(cardNumber)));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> antiFraudService.addStolenCard(requestDTO));
        assertEquals("This card number is already in use", exception.getMessage());
        verify(stolenCardRepo, times(0)).save(any(StolenCard.class));
    }

    @Test
    void testRemoveStolenCardSuccess() {
        // Given
        String cardNumber = "1234567812345678";
        StolenCard stolenCard = new StolenCard(cardNumber);
        when(stolenCardRepo.findByNumber(cardNumber)).thenReturn(Optional.of(stolenCard));

        // When
        ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> response = antiFraudService.removeStolenCard(cardNumber);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        // Change the assertion to check the content of the body, not the status
        assertTrue(response.getBody().getStatus().contains(cardNumber));
        verify(stolenCardRepo, times(1)).delete(stolenCard);
    }

    @Test
    void testRemoveStolenCardNotFound() {
        // Given
        String cardNumber = "1234567812345678";
        when(stolenCardRepo.findByNumber(cardNumber)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> antiFraudService.removeStolenCard(cardNumber));
        assertEquals("The specified card number (1234567812345678) was not found.", exception.getMessage());
        verify(stolenCardRepo, times(0)).delete(any(StolenCard.class));
    }

    @Test
    void shouldReturnAllSuspiciousIps_WhenCalled() {
        // Given
        List<SuspiciousIp> suspiciousIps = Arrays.asList(
                new SuspiciousIp("192.168.1.1"),
                new SuspiciousIp("192.168.1.2")
        );
        when(suspiciousIpRepo.findAllByOrderByIdAsc()).thenReturn(suspiciousIps);

        // When
        ResponseEntity<List<SuspiciousIp>> response = antiFraudService.getSuspiciousIps();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("192.168.1.1", response.getBody().get(0).getIp());
        assertEquals("192.168.1.2", response.getBody().get(1).getIp());
        verify(suspiciousIpRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void shouldReturnAllStolenCards_WhenCalled() {
        // Given
        List<StolenCard> stolenCards = Arrays.asList(
                new StolenCard("1234567812345678"),
                new StolenCard("8765432187654321")
        );
        when(stolenCardRepo.findAllByOrderByIdAsc()).thenReturn(stolenCards);

        // When
        ResponseEntity<List<StolenCard>> response = antiFraudService.getStolenCards();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("1234567812345678", response.getBody().get(0).getNumber());
        assertEquals("8765432187654321", response.getBody().get(1).getNumber());
        verify(stolenCardRepo, times(1)).findAllByOrderByIdAsc();
    }
}
