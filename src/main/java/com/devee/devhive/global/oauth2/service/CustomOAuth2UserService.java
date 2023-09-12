package com.devee.devhive.global.oauth2.service;


import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.global.oauth2.domain.CustomOAuth2User;
import com.devee.devhive.global.oauth2.domain.OAuthAttributes;
import com.devee.devhive.global.oauth2.domain.dto.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final HttpSession httpSession;

  private static final String NAVER = "naver";
  private static final String KAKAO = "kakao";

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
    OAuth2User oAuth2User = super.loadUser(userRequest);

    // oAuth2 서비스 ID
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    ProviderType providerType = getProviderType(registrationId);
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    Map<String, Object> attributes = oAuth2User.getAttributes();

    //ProviderType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
    OAuthAttributes extractAttributes = OAuthAttributes.of(providerType, userNameAttributeName, attributes);

    User user = getUser(extractAttributes, providerType);
    SessionUserDto userDto = new SessionUserDto(user);
    httpSession.setAttribute("user", userDto);
    log.info("유저 정보 만들기");
    return new CustomOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())),
        attributes,
        extractAttributes.getNameAttributeKey(),
        user.getEmail(),
        user.getRole());
  }

  private ProviderType getProviderType(String registrationId) {
    if (NAVER.equals(registrationId)) {
      return ProviderType.NAVER;
    } else if (KAKAO.equals(registrationId)) {
      return ProviderType.KAKAO;
    }
    return ProviderType.GOOGLE;
  }

  private User getUser(OAuthAttributes attributes, ProviderType providerType) {
    User findUser = userRepository.findByProviderTypeAndProviderId(
        providerType, attributes.getOauth2UserInfo().getProviderId()
        ).orElse(null);

    if (findUser == null) {
      return createSave(attributes, providerType);
    }
    return findUser;
  }

  private User createSave(OAuthAttributes attributes, ProviderType providerType) {
    User user = attributes.toEntity(providerType, attributes.getOauth2UserInfo());
    log.info("유저 정보 저장");
    return userRepository.save(user);
  }
}
