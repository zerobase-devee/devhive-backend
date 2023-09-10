package com.devee.devhive.domain.user.favorite.entity.dto;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {

  private Long favoriteId;
  private Long userId;
  private String nickName;
  private String profileImage;

  public static FavoriteDto from(Favorite favorite) {
    User favoriteUser = favorite.getFavoriteUser();
    return FavoriteDto.builder()
        .favoriteId(favorite.getId())
        .userId(favoriteUser.getId())
        .nickName(favoriteUser.getNickName())
        .profileImage(favoriteUser.getProfileImage())
        .build();
  }
}
