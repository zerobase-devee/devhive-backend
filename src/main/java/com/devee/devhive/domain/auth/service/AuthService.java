package com.devee.devhive.domain.auth.service;

import static com.devee.devhive.domain.user.type.ActivityStatus.ACTIVITY;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.EXPIRED_VERIFY_CODE;
import static com.devee.devhive.global.exception.ErrorCode.INCORRECT_VERIFY_CODE;

import com.devee.devhive.domain.auth.dto.EmailDTO;
import com.devee.devhive.domain.auth.dto.JoinDTO;
import com.devee.devhive.domain.auth.service.mail.MailService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final MailService mailService;
  private final RedisUtil redisUtil;
  private final PasswordEncoder passwordEncoder;

  // 인증 코드
  public void getVerificationCode(EmailDTO emailDTO) throws Exception {

    mailService.sendAuthEmail(emailDTO.getEmail());
  }

  // 유저 회원가입
  @Transactional
  public void signUp(JoinDTO joinDTO) {

    if (userRepository.existsByEmail(joinDTO.getEmail())) {
      throw new CustomException(DUPLICATE_EMAIL);
    }
    if (userRepository.existsByNickName(joinDTO.getNickName())) {
      throw new CustomException(DUPLICATE_NICKNAME);
    }

    User user = User.builder()
        .region(joinDTO.getRegion())
        .email(joinDTO.getEmail())
        .password(passwordEncoder.encode(joinDTO.getPassword()))
        .nickName(joinDTO.getNickName())
        .status(ACTIVITY)
        .build();

    String enteredCode = joinDTO.getVerificationCode();
    String cachedCode = redisUtil.getData(joinDTO.getEmail());

    if (!redisUtil.existData(joinDTO.getEmail())) {
      throw new CustomException(EXPIRED_VERIFY_CODE);
    }
    if (!cachedCode.equals(enteredCode)) {
      throw new CustomException(INCORRECT_VERIFY_CODE);
    }

    userRepository.save(user);
  }
}
