package antifraud.repo;

import antifraud.model.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByDateGreaterThanEqualAndDateLessThanAndNumber
            (LocalDateTime startDate, LocalDateTime endDate, String number);
    Optional<Transaction> getById(long id);
    List<Transaction> findAllByOrderByIdAsc();
    List<Transaction> findAllByNumberOrderByIdAsc(String number);
}
