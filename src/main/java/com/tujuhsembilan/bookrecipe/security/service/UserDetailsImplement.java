package com.tujuhsembilan.bookrecipe.security.service;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tujuhsembilan.bookrecipe.model.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImplement implements UserDetails {

    private int id;
    private String username;
    private String password;

    private Collection<? extends GrantedAuthority> authorities;
    
    public static UserDetailsImplement build(Users user) {
    // List<GrantedAuthority> authorities = user.getRole().stream()
    //     .map(role -> new SimpleGrantedAuthority(role.getName().name()))
    //     .collect(Collectors.toList());
    List<GrantedAuthority> authorities = new ArrayList<>();;
    authorities.add(new SimpleGrantedAuthority(user.getRole()));

    return new UserDetailsImplement(
        user.getUserId(), 
        user.getUsername(), 
        user.getPassword(), 
        authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAccountNonExpired'");
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAccountNonLocked'");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCredentialsNonExpired'");
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEnabled'");
    }
  
}