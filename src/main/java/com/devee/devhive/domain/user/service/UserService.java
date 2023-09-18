package com.devee.devhive.domain.user.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CHANGED_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.NEW_PASSWORD_MISMATCH_RE_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_EQUALS_NEW_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_MISMATCH;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final S3Service s3Service;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
  }

  // 랭킹 목록 조회
  public Page<User> getRankUsers(Pageable pageable) {
    return userRepository.findAllByOrderByRankPointDesc(pageable);
  }

  // 프로필 사진 수정
  @Transactional
  public void updateProfileImage(MultipartFile multipartFile, User user) {
    // 기존 프로필 있으면 s3에 저장한 이미지 삭제
    String currentProfileImage = user.getProfileImage();
    if (currentProfileImage != null && !currentProfileImage.isEmpty()) {
      String imageUrl = URLDecoder.decode(currentProfileImage, StandardCharsets.UTF_8);
      String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

      s3Service.delete(filename);
    }
    String image = s3Service.upload(multipartFile);
    user.setProfileImage(image);

    userRepository.save(user);
  }

  // 내 프로필 사진 삭제
  public void deleteProfileImage(User user) {
    String imageUrl = URLDecoder.decode(user.getProfileImage(), StandardCharsets.UTF_8);
    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

    s3Service.delete(filename);
    user.setProfileImage(null);
    userRepository.save(user);
  }

  // 내 기본 정보 수정
  @Transactional
  public void updateBasicInfo(User user, UpdateBasicInfoForm form) {
    // 닉네임은 소셜로그인한 유저가 1번만 변경 가능
    String nickname = form.getNickName();
    String userNickname = user.getNickName();
    if (!userNickname.equals(nickname)) {
      // 최초 닉네임인 경우에만 변경
      if (userNickname.startsWith("닉네임변경해주세요")) {
        updateNickname(user, nickname);
      } else {
        throw new CustomException(ALREADY_CHANGED_NICKNAME);
      }
    }

    // 지역
    String newRegion = form.getRegion();
    if (!Objects.equals(newRegion, user.getRegion())) {
      user.setRegion(newRegion);
    }

    // 자기소개
    String newIntro = form.getIntro();
    if (!Objects.equals(newIntro, user.getIntro())) {
      user.setIntro(newIntro);
    }

    userRepository.save(user);
  }

  // 내 닉네임 변경
  private void updateNickname(User user, String nickname) {
    if (userRepository.existsByNickName(nickname)) {
      throw new CustomException(DUPLICATE_NICKNAME);
    }
    user.setNickName(nickname);
    userRepository.save(user);
  }

  // 비밀번호 변경
  public void updatePassword(User user, UpdatePasswordForm form) {
    String userPassword = user.getPassword();
    String password = form.getPassword();
    String newPassword = form.getNewPassword();

    // 실제 비밀번호와 입력한 유저 비밀번호 다르면 예외 발생
    if (!passwordEncoder.matches(password, userPassword)) {
      throw new CustomException(USER_PASSWORD_MISMATCH);
    }
    // 새 비밀번호와 새 비밀번호 확인 이 다르면 예외 발생
    if (!newPassword.equals(form.getRePassword())) {
      throw new CustomException(NEW_PASSWORD_MISMATCH_RE_PASSWORD);
    }
    // 기존 비밀번호랑 새 비밀번호 같으면 예외 발생, 다르면 변경
    if (password.equals(newPassword)) {
      throw new CustomException(USER_PASSWORD_EQUALS_NEW_PASSWORD);
    } else {
      user.setPassword(passwordEncoder.encode(newPassword));
      userRepository.save(user);
    }
  }

  // 랭킹포인트 업데이트, 알림이벤트 발행
  @Transactional
  public void updateRankPoint(User user, Project project, double averagePoint) {
    user.setRankPoint(user.getRankPoint() + averagePoint);
    userRepository.save(user);

    // 평가 완료 알림 이벤트 발행
    AlarmForm alarmForm = AlarmForm.builder()
        .receiverUser(user)
        .projectId(project.getId())
        .projectName(project.getName())
        .content(AlarmContent.REVIEW_RESULT)
        .build();
    eventPublisher.publishEvent(alarmForm);
  }

  // 유저 활성화/비활성화
  public void setUserStatus(User user, ActivityStatus status) {
    user.setStatus(status);
    userRepository.save(user);
  }
}
