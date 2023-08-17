package com.devee.devhive.domain.user.service;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CHANGED_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.devee.devhive.global.exception.ErrorCode.NEW_PASSWORD_MISMATCH_RE_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_EQUALS_NEW_PASSWORD;
import static com.devee.devhive.global.exception.ErrorCode.USER_PASSWORD_MISMATCH;

import com.devee.devhive.domain.project.member.repository.ProjectMemberRepository;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.domain.user.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.UserTechStack;
import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.ProjectHistoryDto;
import com.devee.devhive.domain.user.entity.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.dto.UserInformationDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdateEtcInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.exithistory.repository.ExitHistoryRepository;
import com.devee.devhive.domain.user.repository.UserBadgeRepository;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.repository.UserTechStackRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import com.devee.devhive.global.util.RedisUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ExitHistoryRepository exitHistoryRepository;
    private final CareerRepository careerRepository;
    private final TechStackRepository techStackRepository;

    private final S3Service s3Service;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    public MyInfoDto getMyInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        UserInformationDto userInformation = getUserInformation(user);
        return MyInfoDto.of(user, userInformation);
    }

    // 다른 유저 정보 조회
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)

            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        UserInformationDto userInformation = getUserInformation(user);
        return UserInfoDto.of(user, userInformation);
    }

    private UserInformationDto getUserInformation(User user) {
        List<TechStackDto> techStacks = userTechStackRepository.findAllByUser(user).stream()
            .map(techStack -> TechStackDto.from(techStack.getTechStack()))
            .collect(Collectors.toList());
        List<CareerDto> careers = careerRepository.findAllByUserOrderByStartDateAsc(user).stream()
            .map(CareerDto::from)
            .collect(Collectors.toList());
        List<BadgeDto> badges = userBadgeRepository.findAllByUser(user).stream()
            .map(badge -> BadgeDto.from(badge.getBadge()))
            .collect(Collectors.toList());
        List<ProjectHistoryDto> projectHistories =
            projectMemberRepository.getProjectNamesAndTotalScoresByUser(user)
                .stream()
                .map(ProjectHistoryDto::from)
                .collect(Collectors.toList());
        int hiveLevel = projectMemberRepository.countCompletedProjectsByUser(user);
        int exitNum = exitHistoryRepository.countExitHistoryByUser(user);

        return UserInformationDto.of(techStacks, careers, badges, projectHistories, hiveLevel, exitNum);
    }

    // 프로필 사진 수정
    @Transactional
    public void updateProfileImage(MultipartFile multipartFile, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

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
    @Transactional
    public void deleteProfileImage(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        String imageUrl = URLDecoder.decode(user.getProfileImage(), StandardCharsets.UTF_8);
        String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

        s3Service.delete(filename);
        user.setProfileImage(null);
        userRepository.save(user);
    }

    // 내 기본 정보 수정
    @Transactional
    public void updateBasicInfo(Authentication authentication, UpdateBasicInfoForm form) {
        User user = (User) authentication.getPrincipal();

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
    @Transactional
    public void updateNickname(User user, String nickname) {
        try {
            boolean nicknameLocked = redisUtil.getLock(nickname, 5);
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
            redisUtil.unLock(nickname);
        }
    }

    // 내 기타 정보 수정(경력, 기술스택)
    @Transactional
    public void updateEtcInfo(Authentication authentication, UpdateEtcInfoForm form) {
        User user = (User) authentication.getPrincipal();

        updateTechStacks(user, form.getTechStacks());
        updateCareers(user, form.getCareerDtoList());
    }

    private void updateTechStacks(User user, List<TechStackDto> newTechStacks) {
        List<UserTechStack> existingTechStacks = user.getUserTechStacks();

        // 기존 유저기술스택이 비었다면 요청된 기술스택 바로 저장
        if (existingTechStacks.isEmpty()) {
            List<UserTechStack> newUserTechStacks = newTechStacks.stream()
                .map(techStackDto -> UserTechStack.of(user, TechStack.from(techStackDto)))
                .collect(Collectors.toList());
            userTechStackRepository.saveAll(newUserTechStacks);
        } else if (newTechStacks.isEmpty()) {
            // 요청된 기술스택이 비었다면 기존 유저기술스택 모두 삭제
            userTechStackRepository.deleteAll(user.getUserTechStacks());
        }else {
            // 기존 유저기술스택 삭제할거 삭제, 추가할거 추가 저장
            List<Long> newTechStackIds = newTechStacks.stream()
                .map(TechStackDto::getId)
                .collect(Collectors.toList());
            List<Long> techStackIdsToDelete = new ArrayList<>();

            for (UserTechStack userTechStack : existingTechStacks) {
                Long curExistingId = userTechStack.getTechStack().getId();
                if (newTechStackIds.contains(curExistingId)) {
                    newTechStackIds.remove(curExistingId);
                } else {
                    techStackIdsToDelete.add(curExistingId);
                }
            }

            if (!techStackIdsToDelete.isEmpty()) {
                userTechStackRepository.deleteAllByUserAndTechStackIdIn(user, techStackIdsToDelete);
            }

            if (!newTechStackIds.isEmpty()) {
                List<TechStack> techStacks = techStackRepository.findAllById(newTechStackIds);
                for (TechStack techStack : techStacks) {
                    userTechStackRepository.save(UserTechStack.of(user, techStack));
                }
            }
        }
    }

    private void updateCareers(User user, List<CareerDto> newCareers) {
        List<Career> existingCareers = user.getUserCareers();

        // 기존 유저경력 비었다면 요청된 경력 바로 저장
        if (existingCareers.isEmpty()) {
            careerRepository.saveAll(newCareers.stream()
                .map(careerDto -> Career.of(user, careerDto))
                .collect(Collectors.toList()));
        } else if (newCareers.isEmpty()) {
            // 요청 경력이 비었다면 기존 유저경력 모두 삭제
            careerRepository.deleteAll(user.getUserCareers());
        } else {
            // 기존 유저경력 삭제할거 삭제, 추가할거 추가 저장
            List<Career> careersToDelete = existingCareers.stream()
                .filter(career -> newCareers.stream()
                    .noneMatch(careerDto ->
                        careerDto.equals(CareerDto.from(career))))
                .toList();

            if (!careersToDelete.isEmpty()) {
                careerRepository.deleteAll(careersToDelete);
            }

            List<CareerDto> careersToAdd = newCareers.stream()
                .filter(careerDto -> existingCareers.stream()
                    .noneMatch(existingCareer ->
                        CareerDto.from(existingCareer).equals(careerDto)))
                .toList();

            if (!careersToAdd.isEmpty()) {
                careerRepository.saveAll(newCareers.stream()
                    .map(careerDto -> Career.of(user, careerDto))
                    .collect(Collectors.toList()));
            }
        }
    }

    // 랭킹 목록 조회
    public Page<RankUserDto> getRankUsers(Pageable pageable) {
        return userRepository.findAllByOrderByRankPointDesc(pageable)
            .map(RankUserDto::from);
    }

    // 비밀번호 변경
    public void updatePassword(Authentication authentication, UpdatePasswordForm form) {
        User user = (User) authentication.getPrincipal();

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
