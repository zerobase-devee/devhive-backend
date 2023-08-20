package com.devee.devhive.global.security.service;

import com.devee.devhive.domain.user.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> oauthUserAttributes;

    // 일반로그인
    public PrincipalDetails(User user){
        this.user = user;
    }
    // oauth2로그인
    public PrincipalDetails(User user, Map<String, Object> oauthUserAttributes) {
        this.user = user;
        this.oauthUserAttributes = oauthUserAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add((GrantedAuthority) () -> user.getRole().getValue());
        return collect;
    }

    // oauth2User
    @Override
    public String getName() {
        return user.getEmail();
    }

    // oauth2User
    @Override
    public Map<String, Object> getAttributes() {
        return oauthUserAttributes;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
