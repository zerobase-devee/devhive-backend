package com.devee.devhive.global.oauth2.service;


import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.domain.user.type.Role;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.InactivityException;
import com.devee.devhive.global.exception.OAuthProviderMissMatchException;
import com.devee.devhive.global.oauth2.info.OAuth2UserInfo;
import com.devee.devhive.global.oauth2.info.OAuth2UserInfoFactory;
import com.devee.devhive.global.security.service.TokenService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final TokenService tokenService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
    OAuth2User oAuth2User = super.loadUser(userRequest);
    return process(userRequest, oAuth2User);
  }

  //인증을 요청하는 사용자에 따라서 없는 회원이면 회원가입
  private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
    ProviderType providerType = ProviderType.valueOf(
        userRequest.getClientRegistration().getRegistrationId().toUpperCase());

    //provider타입에 따라서 각각 다르게 userInfo가져온다. (가져온 필요한 정보는 OAuth2UserInfo로 동일하다)
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

    User savedUser = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

    if (savedUser != null) {
      if (savedUser.getStatus() == ActivityStatus.INACTIVITY) {
        throw new InactivityException("퇴출 전적으로 인해 로그인 비활성 상태입니다!");
      }
      ProviderType userProviderType = savedUser.getProviderType();
      if (providerType != userProviderType) {
        throw new OAuthProviderMissMatchException(
            userProviderType + "로 회원가입한 계정입니다." + userProviderType + "로 로그인 해주세요."
        );
      }
    } else {
      savedUser = createUser(userInfo, providerType);
    }

    return PrincipalDetails.create(savedUser, user.getAttributes());
  }

  // 회원가입
  private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
    String nickname = "닉네임변경해주세요" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    while (userRepository.existsByNickName(nickname)) {
      nickname = "닉네임변경해주세요" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
    String refreshToken = tokenService.createRefreshToken();
    return userRepository.save(User.builder()
            .nickName(nickname)
            .email(userInfo.getEmail())
            .role(Role.USER)
            .providerType(providerType)
            .rankPoint(0.0)
            .status(ActivityStatus.ACTIVITY)
            .refreshToken(refreshToken)
            .build());
  }
}