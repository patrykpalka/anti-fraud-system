package antifraud.repo;

import antifraud.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StolenCardRepo extends JpaRepository<StolenCard, Long> {
    Optional<StolenCard> findByNumber(String cardNumber);
    List<StolenCard> findAllByOrderByIdAsc();
    @Query("SELECT s.number FROM StolenCard s")
    List<String> findAllCardNumbers();
}
