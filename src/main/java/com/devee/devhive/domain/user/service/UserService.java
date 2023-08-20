package com.devee.devhive.domain.user.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CHANGED_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.NEW_PASSWORD_MISMATCH_RE_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_EQUALS_NEW_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_MISMATCH;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.redis.RedisService;
import com.devee.devhive.global.s3.S3Service;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
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
        if (!user.getProfileImage().isEmpty() || user.getProfileImage() != null) {
            String imageUrl = URLDecoder.decode(user.getProfileImage(), StandardCharsets.UTF_8);
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
        // 닉네임
        String nickname = form.getNickName();
        if (!user.getNickName().equals(nickname)) {
            if (user.isNickNameChanged()) {
                throw new CustomException(ALREADY_CHANGED_NICKNAME);
            }
            updateNickname(user, nickname);
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
        try {
            boolean nicknameLocked = redisService.getLock(nickname, 5);
            if (nicknameLocked) {
                // 락 확보 성공하면 중복 체크 수행
                if (userRepository.existsByNickName(nickname)) {
                    throw new CustomException(DUPLICATE_NICKNAME);
                }
                user.setNickName(nickname);
                user.setNickNameChanged(true);
                userRepository.save(user);
            } else {
                // 락 확보 실패 시에는 다른 클라이언트가 이미 해당 닉네임의 락을 확보한 것으로 간주
                throw new CustomException(DUPLICATE_NICKNAME); // 중복된 닉네임으로 간주
            }
        } finally {
            // 락 해제
            redisService.unLock(nickname);
        }
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
}
