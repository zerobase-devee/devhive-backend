package com.devee.devhive.domain.user.favorite.controller;

import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public void register(Principal principal, @PathVariable("userId") Long userId) {
        favoriteService.register(principal, userId);
    }

    // 관심 유저 삭제
    @DeleteMapping("/{userId}")
    public void delete(Principal principal, @PathVariable("userId") Long userId) {
        favoriteService.delete(principal, userId);
    }

    // 관심 유저 목록 조회
    @GetMapping
    public ResponseEntity<Page<SimpleUserDto>> getFavoriteUsers(Principal principal) {
        Pageable pageable = PageRequest.of(0, 9);
        return ResponseEntity.ok(favoriteService.getFavoriteUsers(principal, pageable));
    }
}
