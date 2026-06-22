package com.tujuhsembilan.glucoseclamp.security.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tujuhsembilan.glucoseclamp.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImplement implements UserDetails {

    private static final long serialVersionUID = 1387410741020370012L;
    private int id;
    private String username;
    private String name;
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImplement build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // UPDATE: Mengambil roleName dari dalam object Roles
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));

        if (user.getRole() != null && user.getRole().getRoleAccesses() != null) {
            for (com.tujuhsembilan.glucoseclamp.model.RoleAccess roleAccess : user.getRole().getRoleAccesses()) {
                if (roleAccess.getAccessMenu() != null) {
                    String menuName = roleAccess.getAccessMenu().getMenuName();
                    
                    if (Boolean.TRUE.equals(roleAccess.getCanView())) {
                        authorities.add(new SimpleGrantedAuthority(menuName + ":VIEW"));
                    }
                    if (Boolean.TRUE.equals(roleAccess.getCanAdd())) {
                        authorities.add(new SimpleGrantedAuthority(menuName + ":ADD"));
                    }
                    if (Boolean.TRUE.equals(roleAccess.getCanEdit())) {
                        authorities.add(new SimpleGrantedAuthority(menuName + ":EDIT"));
                    }
                    if (Boolean.TRUE.equals(roleAccess.getCanDelete())) {
                        authorities.add(new SimpleGrantedAuthority(menuName + ":DELETE"));
                    }
                }
            }
        }

        return new UserDetailsImplement(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}