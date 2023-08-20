package com.devee.devhive.domain.user.favorite.controller;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import com.devee.devhive.domain.user.service.UserService;
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

/**
 * 마이페이지 - 관심유저 목록 조회
 * 관심유저 등록/해제
 */
@RestController
@RequestMapping("/api/favorite/users")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    // 관심 유저 등록
    @PostMapping("/{userId}")
    public void register(Principal principal, @PathVariable("userId") Long targetUserId) {
        User user = userService.getUserByEmail(principal.getName());
        User favoriteUser = userService.getUserById(targetUserId);
        favoriteService.register(user, favoriteUser);
    }

    // 관심 유저 삭제
    @DeleteMapping("/{userId}")
    public void delete(Principal principal, @PathVariable("userId") Long targetUserId) {
        User user = userService.getUserByEmail(principal.getName());
        favoriteService.delete(user.getId(), targetUserId);
    }

    // 관심 유저 목록 조회
    @GetMapping
    public ResponseEntity<Page<SimpleUserDto>> getFavoriteUsers(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Pageable pageable = PageRequest.of(0, 9);
        return ResponseEntity.ok(
            favoriteService.getFavoriteUsers(user.getId(), pageable)
                .map(favorite -> SimpleUserDto.from(favorite.getFavoriteUser())));
    }
}
