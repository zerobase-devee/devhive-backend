package com.devee.devhive.domain.user.favorite.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_FAVORITE;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.favorite.repository.FavoriteRepository;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

  private final ApplicationEventPublisher eventPublisher;
  private final FavoriteRepository favoriteRepository;

  public Favorite findByUserIdAndFavoriteUserId(Long userId, Long targetUserId) {
    return favoriteRepository.findByUserIdAndFavoriteUserId(userId, targetUserId).orElse(null);
  }

  public void register(User user, User favoriteUser) {
    Favorite favorite = findByUserIdAndFavoriteUserId(user.getId(), favoriteUser.getId());
    if (favorite == null) {
      favoriteRepository.save(Favorite.builder()
          .user(user)
          .favoriteUser(favoriteUser)
          .build());
    }
  }

  public Favorite findById(Long favoriteId) {
    return favoriteRepository.findById(favoriteId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_FAVORITE));
  }
  public void delete(Favorite favorite) {
    favoriteRepository.delete(favorite);
  }

  public Page<Favorite> getFavoriteUsers(Long userId, Pageable pageable) {
    return favoriteRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
  }

  public void favoriteUserUploadAlarmOfProject(User favoriteUser, Long favoriteUserId, Project project) {
    List<Favorite> users = favoriteRepository.findAllByFavoriteUserId(favoriteUserId);
    if (!users.isEmpty()) {
      for (Favorite favorite : users) {
        User user = favorite.getUser();

        // 관심 유저가 프로젝트 업로드한 경우 관심유저로 등록한 유저들에게 알림 이벤트 발행
        AlarmForm alarmForm = AlarmForm.builder()
            .receiverUser(user)
            .project(project)
            .content(AlarmContent.FAVORITE_USER)
            .user(favoriteUser)
            .build();
        eventPublisher.publishEvent(alarmForm);
      }
    }
  }
}
