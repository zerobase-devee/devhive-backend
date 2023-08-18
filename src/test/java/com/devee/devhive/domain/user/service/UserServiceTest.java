package com.devee.devhive.domain.user.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CHANGED_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.NEW_PASSWORD_MISMATCH_RE_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_EQUALS_NEW_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.UserTechStack;
import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdateEtcInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.favorite.repository.FavoriteRepository;
import com.devee.devhive.domain.user.repository.UserBadgeRepository;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.repository.UserTechStackRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import com.devee.devhive.global.util.RedisUtil;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserBadgeRepository userBadgeRepository;
    @Mock
    private UserTechStackRepository userTechStackRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private ExitHistoryRepository exitHistoryRepository;
    @Mock
    private CareerRepository careerRepository;
    @Mock
    private TechStackRepository techStackRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FavoriteRepository favoriteRepository;

    @Test
    @DisplayName("내 프로필 정보 조회 - 성공")
    void testGetMyInfo() {
        //given
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .region("seoul")
            .nickName("cool")
            .profileImage(null)
            .intro(null)
            .build();
        TechStack techStack = TechStack.builder()
            .id(3L)
            .image("java.jpg")
            .name("java")
            .build();
        List<UserTechStack> userTechStack = Collections.singletonList(
            UserTechStack.builder()
                .id(2L)
                .user(user)
                .techStack(techStack)
                .build());
        user.setUserTechStacks(userTechStack);
        List<Object[]> projectHistories = new ArrayList<>();
        projectHistories.add(new Object[] { "project1", 10.5 });
        projectHistories.add(new Object[] { "project2", 15.2 });
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userTechStackRepository.findAllByUser(user)).thenReturn(userTechStack);
        when(careerRepository.findAllByUserOrderByStartDateAsc(user)).thenReturn(List.of());
        when(userBadgeRepository.findAllByUser(user)).thenReturn(List.of());
        when(projectMemberRepository.getProjectNamesAndTotalScoresByUser(user)).thenReturn(projectHistories);
        when(projectMemberRepository.countCompletedProjectsByUser(user)).thenReturn(3);
        when(exitHistoryRepository.countExitHistoryByUser(user)).thenReturn(1);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        MyInfoDto result = userService.getMyInfo(principal);
        //then
        assertEquals(user.getNickName(), result.getNickName());
        assertEquals(3, result.getHiveLevel());
        assertEquals(1, result.getExitNum());
        assertEquals(2, result.getProjectHistories().size());
        assertEquals("java", result.getTechStacks().get(0).getName());
    }

    @Test
    @DisplayName("다른유저 프로필 정보 조회 - 성공")
    void testGetUserInfo() {
        //given
        User user = User.builder()
            .id(3L)
            .email("test@test.com")
            .build();
        User targetUser = User.builder()
            .id(1L)
            .region("seoul")
            .nickName("cool")
            .profileImage(null)
            .intro(null)
            .build();
        TechStack techStack = TechStack.builder()
            .id(3L)
            .image("java.jpg")
            .name("java")
            .build();
        List<UserTechStack> userTechStack = Collections.singletonList(
            UserTechStack.builder()
                .id(2L)
                .user(targetUser)
                .techStack(techStack)
                .build());
        targetUser.setUserTechStacks(userTechStack);
        List<Object[]> projectHistories = new ArrayList<>();
        projectHistories.add(new Object[] { "project1", 10.5 });
        projectHistories.add(new Object[] { "project2", 15.2 });

        Favorite favorite = Favorite.builder().favoriteUser(targetUser).user(user).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(targetUser));
        when(userTechStackRepository.findAllByUser(targetUser)).thenReturn(userTechStack);
        when(careerRepository.findAllByUserOrderByStartDateAsc(targetUser)).thenReturn(List.of());
        when(userBadgeRepository.findAllByUser(targetUser)).thenReturn(List.of());
        when(projectMemberRepository.getProjectNamesAndTotalScoresByUser(targetUser)).thenReturn(projectHistories);
        when(projectMemberRepository.countCompletedProjectsByUser(targetUser)).thenReturn(3);
        when(exitHistoryRepository.countExitHistoryByUser(targetUser)).thenReturn(1);
        when(favoriteRepository.findByUserAndFavoriteUser(user, targetUser)).thenReturn(Optional.of(favorite));
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        UserInfoDto result = userService.getUserInfo(principal, targetUser.getId());
        //then
        assertEquals(targetUser.getNickName(), result.getNickName());
        assertEquals(3, result.getHiveLevel());
        assertEquals(1, result.getExitNum());
        assertEquals(2, result.getProjectHistories().size());
        assertEquals("java", result.getTechStacks().get(0).getName());
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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Service.upload(multipartFile)).thenReturn("new_profile_image.jpg");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        userService.updateProfileImage(multipartFile, principal);
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
            .nickName("cool")
            .isNickNameChanged(false)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(redisUtil.getLock(anyString(), anyLong())).thenReturn(true);
        when(userRepository.existsByNickName(anyString())).thenReturn(false);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        userService.updateBasicInfo(principal, form);
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
            .isNickNameChanged(true)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updateBasicInfo(principal, form));

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
            .nickName("cool")
            .isNickNameChanged(false)
            .intro(null)
            .build();

        UpdateBasicInfoForm form = UpdateBasicInfoForm.builder()
            .nickName("cool2")
            .intro("안녕하세요")
            .region("deaGu")
            .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(redisUtil.getLock(anyString(), anyLong())).thenReturn(true);
        when(userRepository.existsByNickName(anyString())).thenReturn(true);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updateBasicInfo(principal, form));

        // then
        assertEquals(DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("내 기타 정보 수정 - 성공")
    void testUpdateEtcInfo() {
        //given
        User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .build();

        user.setUserTechStacks(List.of(UserTechStack.builder()
            .id(2L)
            .user(user)
            .techStack(TechStack.builder()
                .id(3L)
                .image("java.jpg")
                .name("java")
                .build())
            .build(), UserTechStack.builder()
            .id(5L)
            .user(user)
            .techStack(TechStack.builder()
                .id(5L)
                .image("react.jpg")
                .name("react")
                .build())
            .build()));
        Career career = Career.builder()
            .company("zero-base")
            .position("intern")
            .startDate(LocalDateTime.of(2023,2,14,0,0,0))
            .build();
        user.setUserCareers(List.of(career));

        List<TechStackDto> techStackDtoList = List.of(
            TechStackDto.builder()
                .id(5L)
                .image("react.jpg")
                .name("react")
                .build(),
            TechStackDto.builder()
                .id(1L)
                .image("python.jpg")
                .name("python")
                .build()
        );
        List<CareerDto> careerDtoList = List.of(
            CareerDto.builder()
                .company("zero-base")
                .position("intern")
                .startDate(LocalDateTime.of(2023,2,14,0,0,0))
                .build(),
            CareerDto.builder()
                .company("google")
                .position("teamReader")
                .startDate(LocalDateTime.of(2020,2,14,0,0,0))
                .endDate(LocalDateTime.of(2022,12,14,0,0,0))
                .build()
        );

        UpdateEtcInfoForm form = UpdateEtcInfoForm.builder()
            .techStacks(techStackDtoList)
            .careerDtoList(careerDtoList)
            .build();

        List<TechStack> delete = List.of(TechStack.builder()
            .id(3L)
            .image("java.jpg")
            .name("java")
            .build());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(techStackRepository.findAllById(anyList())).thenReturn(delete);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        userService.updateEtcInfo(principal,form);
        //then
        verify(userTechStackRepository, times(1)).deleteAllByUserAndTechStackIdIn(any(User.class), anyList());
        verify(userTechStackRepository, times(1)).save(any(UserTechStack.class));
        verify(careerRepository, never()).deleteAll();
        verify(careerRepository, times(1)).saveAll(anyList());
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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        userService.updatePassword(principal, form);
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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(principal, form));

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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(principal, form));

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

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(principal, form));

        // then
        assertEquals(USER_PASSWORD_EQUALS_NEW_PASSWORD, exception.getErrorCode());
    }
}