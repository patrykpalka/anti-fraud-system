package antifraud.service;

import antifraud.dto.request.SuspiciousIpRequestDTO;
import antifraud.dto.request.StolenCardRequestDTO;
import antifraud.dto.response.AntiFraudDeletionResponseDTO;
import antifraud.exception.BadRequestException;
import antifraud.exception.ConflictException;
import antifraud.exception.NotFoundException;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.repo.StolenCardRepo;
import antifraud.repo.SuspiciousIpRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static antifraud.service.utils.ValidationUtil.*;

@Service
@RequiredArgsConstructor
public class AntiFraudService {

    private final SuspiciousIpRepo suspiciousIpRepo;
    private final StolenCardRepo stolenCardRepo;

    @Transactional
    public ResponseEntity<SuspiciousIp> addSuspiciousIp(SuspiciousIpRequestDTO requestDTO) {
        return addEntity(requestDTO, SuspiciousIpRequestDTO::toSuspiciousIp,
                suspiciousIpRepo::findByIp, suspiciousIpRepo::save, "IP address");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<SuspiciousIp>> getSuspiciousIps() {
        return ResponseEntity.ok(suspiciousIpRepo.findAllByOrderByIdAsc());
    }

    @Transactional
    public ResponseEntity<AntiFraudDeletionResponseDTO<SuspiciousIp>> removeSuspiciousIp(String ip) {
        return removeEntity(ip, suspiciousIpRepo::findByIp, suspiciousIpRepo::delete, "IP address");
    }

    @Transactional
    public ResponseEntity<StolenCard> addStolenCard(StolenCardRequestDTO requestDTO) {
        return addEntity(requestDTO, StolenCardRequestDTO::toStolenCard,
                stolenCardRepo::findByNumber, stolenCardRepo::save, "card number");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        return ResponseEntity.ok(stolenCardRepo.findAllByOrderByIdAsc());
    }

    @Transactional
    public ResponseEntity<AntiFraudDeletionResponseDTO<StolenCard>> removeStolenCard(String number) {
        return removeEntity(number, stolenCardRepo::findByNumber, stolenCardRepo::delete, "card number");
    }

    private <T, R> ResponseEntity<T> addEntity(R requestDTO, Function<R, T> toEntity, Function<String,
            Optional<T>> findByField, Consumer<T> saveEntity, String entityType) {
        String field = getFieldFromDTO(requestDTO);

        if (!isValidField(field, entityType)) {
            throw new BadRequestException("Invalid " + entityType);
        }

        if (findByField.apply(field).isPresent()) {
            throw new ConflictException("This " + entityType + " is already in use");
        }

        T entity = toEntity.apply(requestDTO);
        saveEntity.accept(entity);

        return ResponseEntity.ok(entity);
    }

    private String getFieldFromDTO(Object dto) {
        if (dto instanceof SuspiciousIpRequestDTO) {
            return ((SuspiciousIpRequestDTO) dto).getIp();
        } else if (dto instanceof StolenCardRequestDTO) {
            return ((StolenCardRequestDTO) dto).getNumber();
        }
        throw new IllegalArgumentException("Unsupported DTO type");
    }

    private <T> ResponseEntity<AntiFraudDeletionResponseDTO<T>> removeEntity(String field, Function<String,
            Optional<T>> findByField, Consumer<T> deleteEntity, String entityType) {
        if (!isValidField(field, entityType)) {
            throw new BadRequestException("Invalid " + entityType);
        }

        Optional<T> entityOptional = findByField.apply(field);

        if (entityOptional.isEmpty()) {
            throw new NotFoundException(entityType + " not found");
        }

        T entity = entityOptional.get();
        deleteEntity.accept(entity);

        return ResponseEntity.ok(new AntiFraudDeletionResponseDTO<>(entity));
    }
}
