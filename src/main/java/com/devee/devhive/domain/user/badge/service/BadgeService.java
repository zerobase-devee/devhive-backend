package com.devee.devhive.domain.user.badge.service;

import com.devee.devhive.domain.user.badge.entity.Badge;
import com.devee.devhive.domain.user.badge.entity.dto.CreateBadgeDto;
import com.devee.devhive.domain.user.badge.repository.BadgeRepository;
import com.devee.devhive.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeRepository badgeRepository;
  private final S3Service s3Service;

  public void createBadge(CreateBadgeDto badgeDto, MultipartFile imageFile) {
    String imageUrl = s3Service.upload(imageFile);

    Badge badge = Badge.builder()
        .name(badgeDto.getName())
        .imageUrl(imageUrl)
        .build();

    badgeRepository.save(badge);
  }
}
