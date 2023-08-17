package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdateEtcInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 랭킹 목록 페이징 처리
    @GetMapping("/rank")
    public ResponseEntity<Page<RankUserDto>> getRankUsers() {
        Pageable pageable = PageRequest.of(1, 3);
        return ResponseEntity.ok(userService.getRankUsers(pageable));
    }

    // 다른 유저 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    // 내 정보 조회
    @GetMapping("/my-profile")
    public ResponseEntity<MyInfoDto> getMyInfo(Authentication authentication){
        return ResponseEntity.ok(userService.getMyInfo(authentication));
    }

    // 내 기본 정보 수정
    @PutMapping("/my-profile/basic")
    public void updateBasicInfo(
        Authentication authentication,
        @RequestBody @Valid UpdateBasicInfoForm form
    ) {
        userService.updateBasicInfo(authentication, form);
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public void updatePassword(
        Authentication authentication,
        @RequestBody @Valid UpdatePasswordForm form
    ) {
        userService.updatePassword(authentication, form);
    }

    // 내 기타 정보 수정 (기술스택, 경력)
    @PutMapping("/my-profile/etc")
    public void updateEtcInfo(Authentication authentication, @RequestBody UpdateEtcInfoForm form) {
        userService.updateEtcInfo(authentication, form);
    }

    // 내 프로필 사진 수정
    @PutMapping("/my-profile/image")
    public void updateProfileImage(
        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
        Authentication authentication
    ) {
        userService.updateProfileImage(multipartFile, authentication);
    }

    // 내 프로필 사진 삭제
    @DeleteMapping("/my-profile/image")
    public void deleteProfileImage(Authentication authentication) {
        userService.deleteProfileImage(authentication);
    }
}
