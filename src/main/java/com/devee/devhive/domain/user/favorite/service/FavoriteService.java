package com.devee.devhive.domain.user.favorite.service;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public boolean isFavorite(Long userId, Long targetUserId) {
        return favoriteRepository.findByUserIdAndFavoriteUserId(
            userId, targetUserId).isPresent();
    }

    public void register(User user, User favoriteUser) {
        favoriteRepository.save(Favorite.builder()
                .user(user)
                .favoriteUser(favoriteUser)
                .build());
    }

    public void delete(Long userId, Long targetUserId) {
        favoriteRepository.findByUserIdAndFavoriteUserId(userId, targetUserId)
            .ifPresent(favoriteRepository::delete);
    }

    public Page<Favorite> getFavoriteUsers(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }
}
