package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.service.CareerService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.ProjectHistoryDto;
import com.devee.devhive.domain.user.entity.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.dto.UserInformationDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdateEtcInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.exithistory.service.ExitHistoryService;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import com.devee.devhive.domain.user.service.UserBadgeService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.service.UserTechStackService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 마이페이지 - 내 프로필, 비밀번호 변경
 * 랭킹 목록 페이지
 * 다른 유저 프로필 페이지
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final UserTechStackService userTechStackService;
    private final CareerService careerService;
    private final UserBadgeService userBadgeService;
    private final ProjectMemberService projectMemberService;
    private final ExitHistoryService exitHistoryService;
    private final ProjectReviewService projectReviewService;

    // 다른 유저 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable("userId") Long targetUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User targetUser = userService.getUserById(targetUserId);
        UserInformationDto informationDto = getUserInformation(targetUserId);
        boolean isFavorite = false; // 비로그인 상태를 기본으로 설정

        if (authentication != null && authentication.isAuthenticated()) {
            // 로그인한 상태일 때 동작
            User user = userService.getUserByEmail(authentication.getName());
            isFavorite = favoriteService.isFavorite(user.getId(), targetUserId);
        }

        return ResponseEntity.ok(UserInfoDto.of(targetUser, informationDto, isFavorite));
    }

    // 내 정보 조회
    @GetMapping("/my-profile")
    public ResponseEntity<MyInfoDto> getMyInfo(Principal principal){
        User user = userService.getUserByEmail(principal.getName());
        UserInformationDto informationDto = getUserInformation(user.getId());

        return ResponseEntity.ok(MyInfoDto.of(user, informationDto));
    }

    // 내 기본 정보 수정
    @PutMapping("/my-profile/basic")
    public void updateBasicInfo(
        Principal principal,
        @RequestBody @Valid UpdateBasicInfoForm form
    ) {
        User user = userService.getUserByEmail(principal.getName());
        userService.updateBasicInfo(user, form);
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public void updatePassword(
        Principal principal,
        @RequestBody @Valid UpdatePasswordForm form
    ) {
        User user = userService.getUserByEmail(principal.getName());
        userService.updatePassword(user, form);
    }

    // 내 기타 정보 수정 (기술스택, 경력)
    @PutMapping("/my-profile/etc")
    public void updateEtcInfo(Principal principal, @RequestBody UpdateEtcInfoForm form) {
        User user = userService.getUserByEmail(principal.getName());
        userTechStackService.updateTechStacks(user, form.getTechStacks());
        careerService.updateCareers(user, form.getCareerDtoList());
    }

    // 내 프로필 사진 수정
    @PutMapping("/my-profile/image")
    public void updateProfileImage(
        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
        Principal principal
    ) {
        User user = userService.getUserByEmail(principal.getName());
        userService.updateProfileImage(multipartFile, user);
    }

    // 내 프로필 사진 삭제
    @DeleteMapping("/my-profile/image")
    public void deleteProfileImage(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        userService.deleteProfileImage(user);
    }

    // 랭킹 목록 페이징 처리
    @GetMapping("/rank")
    public ResponseEntity<Page<RankUserDto>> getRankUsers() {
        Pageable pageable = PageRequest.of(0, 3);
        return ResponseEntity.ok(
            userService.getRankUsers(pageable).map(RankUserDto::from)
        );
    }

    private UserInformationDto getUserInformation(Long userId) {
        List<TechStackDto> techStacks = userTechStackService.getUserTechStacks(userId).stream()
            .map(userTechStack -> TechStackDto.from(userTechStack.getTechStack()))
            .collect(Collectors.toList());
        List<CareerDto> careers = careerService.getUserCareers(userId).stream()
            .map(CareerDto::from)
            .collect(Collectors.toList());
        List<BadgeDto> badges = userBadgeService.getUserBadges(userId).stream()
            .map(badge -> BadgeDto.from(badge.getBadge()))
            .collect(Collectors.toList());

        List<Project> projects = projectMemberService.findAllByUserId(userId).stream()
            .map(ProjectMember::getProject)
            .toList();
        List<ProjectHistoryDto> projectHistories =
            projects.stream().map(project -> ProjectHistoryDto.of(
                project.getName(),
                projectReviewService.getAverageTotalScoreByTargetUserAndProject(userId,project.getId()))
            ).collect(Collectors.toList());

        int hiveLevel = projectMemberService.countCompletedProjectsByUserId(userId);
        int exitNum = exitHistoryService.countExitHistoryByUserId(userId);

        return UserInformationDto.of(techStacks, careers, badges, projectHistories, hiveLevel, exitNum);
    }
}
