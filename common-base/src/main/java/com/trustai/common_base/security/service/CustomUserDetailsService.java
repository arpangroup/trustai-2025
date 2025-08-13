package com.trustai.common_base.security.service;

import com.trustai.common_base.domain.user.Role;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.trustai_common.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(usernameOrEmail)
                .or(() -> userRepo.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        /*User user = new User("admin");
        user.setEmail("john123@doe.com");
        user.setMobile("9812345678");
        user.setFirstname("John");
        user.setId(1L);
        user.setRoles(Set.of(new Role("user")));
        user.setPassword("$2a$12$dokOUjkyRo.YhTOBdmcEUuh82sf4vRcd1BnmcRu18K7vSCd5d4B.S");*/
        return new CustomUserDetails(user);
    }
}
