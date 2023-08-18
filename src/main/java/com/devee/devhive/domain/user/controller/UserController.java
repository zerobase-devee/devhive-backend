package com.devee.devhive.domain.user.controller;

import com.devee.devhive.domain.user.entity.dto.MyInfoDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.form.UpdateBasicInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdateEtcInfoForm;
import com.devee.devhive.domain.user.entity.form.UpdatePasswordForm;
import com.devee.devhive.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // 다른 유저 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(Principal principal, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(principal, userId));
    }

    // 내 정보 조회
    @GetMapping("/my-profile")
    public ResponseEntity<MyInfoDto> getMyInfo(Principal principal){
        return ResponseEntity.ok(userService.getMyInfo(principal));
    }

    // 내 기본 정보 수정
    @PutMapping("/my-profile/basic")
    public void updateBasicInfo(
        Principal principal,
        @RequestBody @Valid UpdateBasicInfoForm form
    ) {
        userService.updateBasicInfo(principal, form);
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public void updatePassword(
        Principal principal,
        @RequestBody @Valid UpdatePasswordForm form
    ) {
        userService.updatePassword(principal, form);
    }

    // 내 기타 정보 수정 (기술스택, 경력)
    @PutMapping("/my-profile/etc")
    public void updateEtcInfo(Principal principal, @RequestBody UpdateEtcInfoForm form) {
        userService.updateEtcInfo(principal, form);
    }

    // 내 프로필 사진 수정
    @PutMapping("/my-profile/image")
    public void updateProfileImage(
        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
        Principal principal
    ) {
        userService.updateProfileImage(multipartFile, principal);
    }

    // 내 프로필 사진 삭제
    @DeleteMapping("/my-profile/image")
    public void deleteProfileImage(Principal principal) {
        userService.deleteProfileImage(principal);
    }
}
