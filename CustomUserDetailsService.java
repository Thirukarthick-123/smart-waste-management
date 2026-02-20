package com.example.smartwaste.security; // <‑‑ IMPORTANT – security package

import com.example.smartwaste.model.Admin;
import com.example.smartwaste.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Loads an {@link Admin} from the DB and builds a Spring‑Security
 * {@link UserDetails}
 * object that is used by the authentication manager.
 *
 * The class is annotated with {@code @Service} so that component‑scanning picks
 * it up
 * (the base scan package is {@code com.example.smartwaste}).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Spring Security expects a collection of GrantedAuthority objects.
        // The role stored in the DB already contains the "ROLE_" prefix.
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword()) // BCrypt hash
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(admin.getRole())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
