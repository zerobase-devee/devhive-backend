package com.devee.devhive.global.security.service;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.InactivityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

    if (user.getStatus() == ActivityStatus.INACTIVITY) {
      throw new InactivityException("퇴출 전적으로 인해 로그인 비활성 상태입니다!");
    }

    return PrincipalDetails.create(user);
  }
}
