package com.devee.devhive.global.oauth2.service;


import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.oauth2.domain.OAuthAttributes;
import com.devee.devhive.global.oauth2.domain.dto.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService
    implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final HttpSession httpSession;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
    OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = service.loadUser(userRequest);

    // oAuth2 서비스 ID
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    OAuthAttributes attributes = OAuthAttributes
        .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    User user = saveOrUpdate(attributes);
    SessionUserDto userDto = new SessionUserDto(user);
    httpSession.setAttribute("user", userDto);

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
        attributes.getAttributes(),
        attributes.getNameAttributeKey());
  }

  private User saveOrUpdate(OAuthAttributes attributes) {
    User user = userRepository.findByEmail(attributes.getEmail())
        .map(u -> u.update(attributes.getName(), attributes.getProfileImage()))
        .orElse(attributes.toEntity());

    return userRepository.save(user);
  }
}
