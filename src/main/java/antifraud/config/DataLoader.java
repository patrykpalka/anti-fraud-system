package antifraud.config;

import antifraud.model.Role;
import antifraud.repo.RoleRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final RoleRepo roleRepo;

    @PostConstruct
    public void init() {
        if (roleRepo.count() == 0) {
            roleRepo.save(new Role("ROLE_MERCHANT"));
            roleRepo.save(new Role("ROLE_ADMINISTRATOR"));
            roleRepo.save(new Role("ROLE_SUPPORT"));
        }
    }
}
