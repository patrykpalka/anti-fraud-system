package antifraud.repo;

import antifraud.model.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SuspiciousIpRepo extends JpaRepository<SuspiciousIp, Long> {
    @Query("SELECT i.ip FROM SuspiciousIp i")
    List<String> findAllIps();
    Optional<SuspiciousIp> findByIp(String ip);
    List<SuspiciousIp> findAllByOrderByIdAsc();
}
