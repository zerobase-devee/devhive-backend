package com.devee.devhive.domain.user.favorite.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndFavoriteUser(User user, User favoriteUser);

    Page<Favorite> findByUserOrderByCreatedDateDesc(User user, Pageable pageable);
}
