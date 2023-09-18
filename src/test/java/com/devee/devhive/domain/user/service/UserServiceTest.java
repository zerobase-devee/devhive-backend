package com.devee.devhive.domain.user.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CHANGED_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.NEW_PASSWORD_MISMATCH_RE_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_EQUALS_NEW_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("프로필사진 수정 - 성공")
    void testUpdateProfileImage() {
        //given
        String existingProfileImage = "existing_profile_image.jpg";
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .profileImage(existingProfileImage)
            .build();
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Service.upload(multipartFile)).thenReturn("new_profile_image.jpg");
        //when
        userService.updateProfileImage(multipartFile, user);
        //then
        verify(s3Service).delete(existingProfileImage);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("내 기본 정보 수정 - 성공")
    void testUpdateBasicInfo() {
        //given
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .region("seoul")
            .nickName("GOOGLE_123")
            .providerType(ProviderType.GOOGLE)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        when(userRepository.existsByNickName(form.getNickName())).thenReturn(false);
        //when
        userService.updateBasicInfo(user, form);
        //then
        verify(userRepository, times(2)).save(user);
    }
    @Test
    @DisplayName("내 기본 정보 수정 - 실패_이미 닉네임 변경한적 있음")
    void testUpdateBasicInfo_Fail_ALREADY_CHANGED_NICKNAME() {
        //given
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .region("seoul")
            .nickName("cool")
            .providerType(ProviderType.GOOGLE)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updateBasicInfo(user, form));

        // then
        assertEquals(ALREADY_CHANGED_NICKNAME, exception.getErrorCode());
    }
    @Test
    @DisplayName("내 기본 정보 수정 - 실패_중복 닉네임")
    void testUpdateBasicInfo_Fail_DUPLICATE_NICKNAME() {
        //given
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .region("seoul")
            .nickName("GOOGLE_123")
            .providerType(ProviderType.GOOGLE)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        when(userRepository.existsByNickName(form.getNickName())).thenReturn(true);
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updateBasicInfo(user, form));

        // then
        assertEquals(DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void testUpdatePassword() {
        //given
        UpdatePasswordForm form = UpdatePasswordForm.builder()
            .password("test1234!")
            .newPassword("test1212@")
            .rePassword("test1212@")
            .build();
        String userPassword = passwordEncoder.encode(form.getPassword());
        User user = User.builder()
            .email("test@test.com")
            .password(userPassword)
            .build();

        when(passwordEncoder.matches(form.getPassword(), user.getPassword())).thenReturn(true);
        //when
        userService.updatePassword(user, form);
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패_실제 비밀번호 != 입력 비밀번호")
    void testUpdatePassword_Fail_USER_PASSWORD_MISMATCH() {
        //given
        UpdatePasswordForm form = UpdatePasswordForm.builder()
            .password("test1234!")
            .newPassword("test1212@")
            .rePassword("test1212@")
            .build();
        User user = User.builder()
            .email("test@test.com")
            .password("adf1234!")
            .build();

        when(passwordEncoder.matches(form.getPassword(), user.getPassword())).thenReturn(false);
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(user, form));

        // then
        assertEquals(USER_PASSWORD_MISMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패_새 비밀번호 != 새 비밀번호 확인")
    void testUpdatePassword_Fail_NEW_PASSWORD_MISMATCH_RE_PASSWORD() {
        //given
        UpdatePasswordForm form = UpdatePasswordForm.builder()
            .password("test1234!")
            .newPassword("bbb1234!")
            .rePassword("aaa1234!")
            .build();
        String userPassword = passwordEncoder.encode(form.getPassword());
        User user = User.builder()
            .email("test@test.com")
            .password(userPassword)
            .build();

        when(passwordEncoder.matches(form.getPassword(), user.getPassword())).thenReturn(true);
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(user, form));

        // then
        assertEquals(NEW_PASSWORD_MISMATCH_RE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패_기존 비밀번호 == 새 비밀번호")
    void testUpdatePassword_Fail_USER_PASSWORD_EQUALS_NEW_PASSWORD() {
        //given
        UpdatePasswordForm form = UpdatePasswordForm.builder()
            .password("test1234!")
            .newPassword("test1234!")
            .rePassword("test1234!")
            .build();
        String userPassword = passwordEncoder.encode(form.getPassword());
        User user = User.builder()
            .email("test@test.com")
            .password(userPassword)
            .build();

        when(passwordEncoder.matches(form.getPassword(), user.getPassword())).thenReturn(true);
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(user, form));

        // then
        assertEquals(USER_PASSWORD_EQUALS_NEW_PASSWORD, exception.getErrorCode());
    }
}