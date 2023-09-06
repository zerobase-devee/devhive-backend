package com.devee.devhive.domain.badge.service;

import static com.devee.devhive.global.exception.ErrorCode.DUPLICATE_BADGE;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_BADGE;

import com.devee.devhive.domain.badge.entity.Badge;
import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.badge.entity.dto.CreateBadgeDto;
import com.devee.devhive.domain.badge.repository.BadgeRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeRepository badgeRepository;
  private final S3Service s3Service;

  public List<BadgeDto> getAllBadges() {
    return badgeRepository.findAll().stream()
        .map(BadgeDto::from)
        .collect(Collectors.toList());
  }

  public void createBadge(CreateBadgeDto badgeDto) {
    if (badgeRepository.existsByName(badgeDto.getName())) {
      throw new CustomException(DUPLICATE_BADGE);
    }

    Badge badge = Badge.builder()
        .name(badgeDto.getName())
        .imageUrl(badgeDto.getImageUrl())
        .build();

    badgeRepository.save(badge);
  }

  public void deleteBadge(Long badgeId) {
    Badge badge = badgeRepository.findById(badgeId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_BADGE));
    String imageUrl = URLDecoder.decode(badge.getImageUrl(), StandardCharsets.UTF_8);
    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

    s3Service.delete(filename);

    badgeRepository.delete(badge);
  }
}
