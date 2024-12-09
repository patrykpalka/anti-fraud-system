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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class AntiFraudServiceTest {

    @Mock
    private SuspiciousIpRepo suspiciousIpRepo;

    @Mock
    private StolenCardRepo stolenCardRepo;

    @InjectMocks
    private AntiFraudService antiFraudService;

    private static final String TEST_IP = "192.168.1.1";
    private static final String TEST_CARD_NUMBER = "1234567812345678";
    private SuspiciousIpRequestDTO suspiciousIpRequestDTO;
    private StolenCardRequestDTO stolenCardRequestDTO;
    private SuspiciousIp suspiciousIp;
    private StolenCard stolenCard;

    @BeforeEach
    void setUp() {
        // Common setup for all tests
        suspiciousIpRequestDTO = new SuspiciousIpRequestDTO();
        suspiciousIpRequestDTO.setIp(TEST_IP);

        stolenCardRequestDTO = new StolenCardRequestDTO();
        stolenCardRequestDTO.setNumber(TEST_CARD_NUMBER);

        suspiciousIp = new SuspiciousIp(TEST_IP);
        stolenCard = new StolenCard(TEST_CARD_NUMBER);
    }

    @Test
    @DisplayName("Should add a suspicious IP successfully")
    void shouldAddSuspiciousIpSuccessfully() {
        // Arrange
        when(suspiciousIpRepo.findByIp(TEST_IP)).thenReturn(Optional.empty());
        when(suspiciousIpRepo.save(any(SuspiciousIp.class))).thenReturn(suspiciousIp);

        // Act
        ResponseEntity<?> response = antiFraudService.addSuspiciousIp(suspiciousIpRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_IP, ((SuspiciousIp) response.getBody()).getIp());
        verify(suspiciousIpRepo, times(1)).save(any(SuspiciousIp.class));
    }

    @Test
    @DisplayName("Should throw conflict exception when adding an already existing suspicious IP")
    void shouldThrowConflictWhenAddingExistingSuspiciousIp() {
        // Arrange
        when(suspiciousIpRepo.findByIp(TEST_IP)).thenReturn(Optional.of(suspiciousIp));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () -> antiFraudService.addSuspiciousIp(suspiciousIpRequestDTO));
        assertEquals("This IP address is already in use", exception.getMessage());
        verify(suspiciousIpRepo, times(0)).save(any(SuspiciousIp.class));
    }

    @Test
    @DisplayName("Should remove a suspicious IP successfully")
    void shouldRemoveSuspiciousIpSuccessfully() {
        // Arrange
        when(suspiciousIpRepo.findByIp(TEST_IP)).thenReturn(Optional.of(suspiciousIp));

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> response = antiFraudService.removeSuspiciousIp(TEST_IP);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getStatus().contains(TEST_IP));
        verify(suspiciousIpRepo, times(1)).delete(suspiciousIp);
    }

    @Test
    @DisplayName("Should throw not found exception when removing a non-existing suspicious IP")
    void shouldThrowNotFoundWhenRemovingNonExistingSuspiciousIp() {
        // Arrange
        when(suspiciousIpRepo.findByIp(TEST_IP)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> antiFraudService.removeSuspiciousIp(TEST_IP));
        assertEquals("The specified IP address (192.168.1.1) was not found.", exception.getMessage());
        verify(suspiciousIpRepo, times(0)).delete(any(SuspiciousIp.class));
    }

    @Test
    @DisplayName("Should add a stolen card successfully")
    void shouldAddStolenCardSuccessfully() {
        // Arrange
        when(stolenCardRepo.findByNumber(TEST_CARD_NUMBER)).thenReturn(Optional.empty());
        when(stolenCardRepo.save(any(StolenCard.class))).thenReturn(stolenCard);

        // Act
        ResponseEntity<?> response = antiFraudService.addStolenCard(stolenCardRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_CARD_NUMBER, ((StolenCard) response.getBody()).getNumber());
        verify(stolenCardRepo, times(1)).save(any(StolenCard.class));
    }

    @Test
    @DisplayName("Should throw conflict exception when adding an already existing stolen card")
    void shouldThrowConflictWhenAddingExistingStolenCard() {
        // Arrange
        when(stolenCardRepo.findByNumber(TEST_CARD_NUMBER)).thenReturn(Optional.of(stolenCard));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () -> antiFraudService.addStolenCard(stolenCardRequestDTO));
        assertEquals("This card number is already in use", exception.getMessage());
        verify(stolenCardRepo, times(0)).save(any(StolenCard.class));
    }

    @Test
    @DisplayName("Should remove a stolen card successfully")
    void shouldRemoveStolenCardSuccessfully() {
        // Arrange
        when(stolenCardRepo.findByNumber(TEST_CARD_NUMBER)).thenReturn(Optional.of(stolenCard));

        // Act
        ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> response = antiFraudService.removeStolenCard(TEST_CARD_NUMBER);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getStatus().contains(TEST_CARD_NUMBER));
        verify(stolenCardRepo, times(1)).delete(stolenCard);
    }

    @Test
    @DisplayName("Should throw not found exception when removing a non-existing stolen card")
    void shouldThrowNotFoundWhenRemovingNonExistingStolenCard() {
        // Arrange
        when(stolenCardRepo.findByNumber(TEST_CARD_NUMBER)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> antiFraudService.removeStolenCard(TEST_CARD_NUMBER));
        assertEquals("The specified card number (1234567812345678) was not found.", exception.getMessage());
        verify(stolenCardRepo, times(0)).delete(any(StolenCard.class));
    }

    @Test
    @DisplayName("Should return all suspicious IPs successfully")
    void shouldReturnAllSuspiciousIps() {
        // Arrange
        List<SuspiciousIp> suspiciousIps = Arrays.asList(new SuspiciousIp("192.168.1.1"), new SuspiciousIp("192.168.1.2"));
        when(suspiciousIpRepo.findAllByOrderByIdAsc()).thenReturn(suspiciousIps);

        // Act
        ResponseEntity<List<SuspiciousIp>> response = antiFraudService.getSuspiciousIps();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("192.168.1.1", response.getBody().get(0).getIp());
        assertEquals("192.168.1.2", response.getBody().get(1).getIp());
        verify(suspiciousIpRepo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("Should return all stolen cards successfully")
    void shouldReturnAllStolenCards() {
        // Arrange
        List<StolenCard> stolenCards = Arrays.asList(new StolenCard("1234567812345678"), new StolenCard("8765432187654321"));
        when(stolenCardRepo.findAllByOrderByIdAsc()).thenReturn(stolenCards);

        // Act
        ResponseEntity<List<StolenCard>> response = antiFraudService.getStolenCards();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("1234567812345678", response.getBody().get(0).getNumber());
        assertEquals("8765432187654321", response.getBody().get(1).getNumber());
        verify(stolenCardRepo, times(1)).findAllByOrderByIdAsc();
    }
}
