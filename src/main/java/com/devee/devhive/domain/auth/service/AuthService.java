package com.devee.devhive.domain.auth.service;

import static com.devee.devhive.domain.user.type.ActivityStatus.ACTIVITY;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.EXPIRED_VERIFY_CODE;
import static com.devee.devhive.global.exception.ErrorCode.INCORRECT_VERIFY_CODE;

import com.devee.devhive.domain.auth.dto.EmailDto;
import com.devee.devhive.domain.auth.dto.JoinDto;
import com.devee.devhive.domain.auth.dto.NicknameDto;
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
  public void getVerificationCode(EmailDto emailDto) throws Exception {

    if (userRepository.existsByEmail(emailDto.getEmail())) {
      throw new CustomException(DUPLICATE_EMAIL);
    }
    mailService.sendAuthEmail(emailDto.getEmail());
  }

  // 유저 회원가입
  @Transactional
  public void signUp(JoinDto joinDto) {

    String enteredCode = joinDto.getVerificationCode();
    String cachedCode = redisUtil.getData(joinDto.getEmail());

    if (!redisUtil.existData(joinDto.getEmail())) {
      throw new CustomException(EXPIRED_VERIFY_CODE);
    }
    if (!cachedCode.equals(enteredCode)) {
      throw new CustomException(INCORRECT_VERIFY_CODE);
    }

    String nickname = joinDto.getNickName();
    try {
      boolean nicknameLocked = redisUtil.getLock(nickname, 5);
      if (nicknameLocked) {
        // 락 확보 성공하면 중복 체크 수행
        if (userRepository.existsByNickName(nickname)) {
          throw new CustomException(DUPLICATE_NICKNAME);
        }
        userRepository.save(User.builder()
            .email(joinDto.getEmail())
            .password(passwordEncoder.encode(joinDto.getPassword()))
            .nickName(joinDto.getNickName())
            .status(ACTIVITY)
            .build());
      }else {
        // 락 확보 실패 시에는 다른 클라이언트가 이미 해당 닉네임의 락을 확보한 것으로 간주
        throw new CustomException(DUPLICATE_NICKNAME); // 중복된 닉네임으로 간주
      }
    } finally {
      // 락 해제
      redisUtil.unLock(nickname);
    }
  }

  public boolean isNicknameAvailable(NicknameDto nicknameDTO) {
    return !userRepository.existsByNickName(nicknameDTO.getNickname());
  }
}
