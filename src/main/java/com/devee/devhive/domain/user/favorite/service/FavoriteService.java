package com.devee.devhive.domain.user.favorite.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.favorite.repository.FavoriteRepository;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.exception.CustomException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public void register(Principal principal, Long targetUserId) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        User favoriteUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        favoriteRepository.save(Favorite.builder()
                .user(user)
                .favoriteUser(favoriteUser)
                .build());
    }

    public void delete(Principal principal, Long targetUserId) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        User favoriteUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        favoriteRepository.findByUserAndFavoriteUser(user, favoriteUser)
            .ifPresent(favoriteRepository::delete);
    }

    public Page<SimpleUserDto> getFavoriteUsers(Principal principal, Pageable pageable) {
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        return favoriteRepository.findByUserOrderByCreatedDateDesc(user, pageable)
            .map(favorite -> SimpleUserDto.from(favorite.getUser()));
    }
}
