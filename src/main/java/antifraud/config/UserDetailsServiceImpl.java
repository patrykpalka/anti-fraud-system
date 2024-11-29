package antifraud.config;

import antifraud.model.AppUser;
import antifraud.model.AppUserAdapter;
import antifraud.repo.AppUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepo appUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = appUserRepo.findByUsername(username);

        if (appUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return new AppUserAdapter(appUser.get());
    }
}
