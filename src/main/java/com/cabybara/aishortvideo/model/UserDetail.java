package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.utils.UserRole;
import org.springframework.security.core.userdetails.UserDetails;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class UserDetail implements UserDetails {

    private String email;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetail(User user) {
        this.email = user.getEmail(); // Use email as username
        this.password = user.getPassword();
        String roleName = user.getRole() == UserRole.USER ? "USER" : "ADMIN";
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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