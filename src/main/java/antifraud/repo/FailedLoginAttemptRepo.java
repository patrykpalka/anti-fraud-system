package antifraud.repo;

import antifraud.model.FailedLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedLoginAttemptRepo extends JpaRepository<FailedLoginAttempt, Long> {

    int countByUsername(String username);
    void deleteByUsername(String username);
}
