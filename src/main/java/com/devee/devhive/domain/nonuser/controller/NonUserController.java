package com.devee.devhive.domain.nonuser.controller;

import com.devee.devhive.domain.nonuser.service.NonUserService;
import com.devee.devhive.domain.nonuser.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 비회원 api
 */
@RestController
@RequestMapping("/api/nonusers")
@RequiredArgsConstructor
public class NonUserController {

    private final NonUserService nonUserService;

    // 랭킹 목록 페이징 처리
    @GetMapping("/rank")
    public ResponseEntity<Page<RankUserDto>> getRankUsers() {
        Pageable pageable = PageRequest.of(0, 3);
        return ResponseEntity.ok(nonUserService.getRankUsers(pageable));
    }

    // 유저 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(nonUserService.getUserInfo(userId));
    }

    // 프로젝트 모집글 목록 조회
    // 프로젝트 모집글 상세 조회
}
