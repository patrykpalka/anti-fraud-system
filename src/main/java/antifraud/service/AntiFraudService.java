package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;

import antifraud.logging.events.antifraud.StolenCardAddedEvent;
import antifraud.logging.events.antifraud.StolenCardRemoveEvent;
import antifraud.logging.events.antifraud.SuspiciousIpAddedEvent;
import antifraud.logging.events.antifraud.SuspiciousIpRemoveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static antifraud.utils.EntityUtils.*;

@Service
@RequiredArgsConstructor
public class AntiFraudService {

    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "suspiciousIps", key = "#requestDTO.ip")
    public ResponseEntity<SuspiciousIp> addSuspiciousIp(SuspiciousIpRequestDTO requestDTO) {
        return addEntity(requestDTO, SuspiciousIpRequestDTO::toSuspiciousIp,
                suspiciousIpRepo::findByIp, suspiciousIpRepo::save, "IP address",
                suspiciousIp -> eventPublisher.publishEvent(new SuspiciousIpAddedEvent(suspiciousIp.getIp())));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable("suspiciousIps")
    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return ResponseEntity.ok(suspiciousIpRepo.findAllByOrderByIdAsc());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "suspiciousIps", key = "#ip")
    public ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> removeSuspiciousIp(String ip) {
        return removeEntity(ip, suspiciousIpRepo::findByIp, suspiciousIpRepo::delete, "IP address",
                suspiciousIp -> eventPublisher.publishEvent(new SuspiciousIpRemoveEvent(suspiciousIp.getIp())));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "stolenCards", key = "#requestDTO.number")
    public ResponseEntity<StolenCard> addStolenCard(StolenCardRequestDTO requestDTO) {
        return addEntity(requestDTO, StolenCardRequestDTO::toStolenCard,
                stolenCardRepo::findByNumber, stolenCardRepo::save, "card number",
                stolenCard -> eventPublisher.publishEvent(new StolenCardAddedEvent(stolenCard.getNumber())));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable("stolenCards")
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return ResponseEntity.ok(stolenCardRepo.findAllByOrderByIdAsc());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "stolenCards", key = "#number")
    public ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> removeStolenCard(String number) {
        return removeEntity(number, stolenCardRepo::findByNumber, stolenCardRepo::delete, "card number",
                stolenCard -> eventPublisher.publishEvent(new StolenCardRemoveEvent(stolenCard.getNumber())));
    }
}
