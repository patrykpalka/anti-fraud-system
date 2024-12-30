package antifraud.repo;

import antifraud.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByDateGreaterThanEqualAndDateLessThanAndNumber
            (LocalDateTime startDate, LocalDateTime endDate, String number);
    Page<Transaction> findAllByOrderByIdAsc(Pageable pageable);
    Page<Transaction> findAllByNumberOrderByIdAsc(String number, Pageable pageable);
}
