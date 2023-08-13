package com.devee.devhive.api.user.favorite.repository;

import com.devee.devhive.api.user.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

}
