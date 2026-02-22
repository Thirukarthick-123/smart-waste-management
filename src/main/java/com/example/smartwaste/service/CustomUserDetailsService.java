package com.example.smartwaste.service;

import com.example.smartwaste.model.Admin;
import com.example.smartwaste.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Loading user: " + username);
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.err.println("❌ User not found in DB: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(admin.getRole())))
                .build();
    }
}
