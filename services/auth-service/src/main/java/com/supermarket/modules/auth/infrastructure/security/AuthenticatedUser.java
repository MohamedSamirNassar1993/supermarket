package com.supermarket.modules.auth.infrastructure.security;

import com.supermarket.modules.auth.domain.Permission;
import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final String email;
    private final UUID organizationId;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.email = user.getEmail();
        this.organizationId = user.getOrganizationId();
        this.active = user.isActive();
        this.authorities = buildAuthorities(user);
    }

    private static Collection<? extends GrantedAuthority> buildAuthorities(User user) {
        Set<String> authorityNames = new HashSet<>();

        for (Role role : user.getRoles()) {
            authorityNames.add("ROLE_" + role.getCode());
            for (Permission permission : role.getPermissions()) {
                authorityNames.add(permission.getCode());
            }
        }

        return authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
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
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
