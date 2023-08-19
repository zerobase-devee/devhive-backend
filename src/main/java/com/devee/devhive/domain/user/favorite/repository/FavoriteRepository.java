package com.devee.devhive.domain.user.favorite.repository;

import com.devee.devhive.domain.user.favorite.entity.Favorite;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndFavoriteUserId(Long userId, Long favoriteUserId);

    Page<Favorite> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);
}
