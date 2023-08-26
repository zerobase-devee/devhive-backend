package com.devee.devhive.domain.user.entity;

import static com.devee.devhive.domain.user.type.Role.USER;

import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.domain.user.type.Role;
import com.devee.devhive.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String nickName;

  private String region;

  private String profileImage;

  private String intro;

  private double rankPoint;

  @Enumerated(EnumType.STRING)
  private ActivityStatus status;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private Role role = USER;

  @LastModifiedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime modifiedDate;

  private String refreshToken;

  private ProviderType providerType;
  private String providerId;

  public void updateRefreshToken(String updateRefreshToken) {
    this.refreshToken = updateRefreshToken;
  }

  @Builder
  public User(String nickName, String password, String email, ProviderType providerType, String providerId) {
    this.nickName = nickName;
    this.email = email;
    this.password = password;
    this.providerType = providerType;
    this.providerId = providerId;
  }
}
