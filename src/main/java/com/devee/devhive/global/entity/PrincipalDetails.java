package com.devee.devhive.global.entity;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.domain.user.type.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final String email;
    private final String password;
    private final ProviderType providerType;
    private final Role role;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // oauth2User
    @Override
    public String getName() {
        return email;
    }

    // oauth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
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

    public static PrincipalDetails create(User user) {
        return new PrincipalDetails(
            user.getEmail(),
            user.getPassword(),
            user.getProviderType(),
            Role.USER,
            Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getValue())));
    }

    public static PrincipalDetails create(User user, Map<String, Object> attributes) {
        PrincipalDetails userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }
}
