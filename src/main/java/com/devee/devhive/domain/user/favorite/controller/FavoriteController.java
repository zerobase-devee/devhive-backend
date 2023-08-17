package com.devee.devhive.domain.user.favorite.controller;

import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorite/users")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 관심 유저 등록
    @PostMapping("/{userId}")
    public void register(@PathVariable("userId") Long targetUserId, Authentication authentication) {
        favoriteService.register(authentication, targetUserId);
    }

    // 관심 유저 삭제
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long targetUserId, Authentication authentication) {
        favoriteService.delete(authentication, targetUserId);
    }

    // 관심 유저 목록 조회
    @GetMapping
    public ResponseEntity<Page<SimpleUserDto>> getFavoriteUsers(
        Authentication authentication) {
        Pageable pageable = PageRequest.of(1, 9);
        return ResponseEntity.ok(favoriteService.getFavoriteUsers(authentication, pageable));
    }
}
