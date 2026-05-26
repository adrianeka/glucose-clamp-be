package com.tujuhsembilan.glucoseclamp.security.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {
    
    @Autowired
    UserRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UPDATE: Menggunakan orElseThrow agar lebih aman jika user tidak ditemukan di database
        User user = usersRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
                
        return UserDetailsImplement.build(user);
    }
}