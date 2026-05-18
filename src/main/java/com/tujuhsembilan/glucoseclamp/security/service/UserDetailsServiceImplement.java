package com.tujuhsembilan.glucoseclamp.security.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.glucoseclamp.model.Users;
import com.tujuhsembilan.glucoseclamp.repository.UsersRepository;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {
    @Autowired
    UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username).get();
        return UserDetailsImplement.build(user);
    }


}