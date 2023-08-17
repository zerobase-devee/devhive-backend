package com.devee.devhive.domain.user.entity;

import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import com.devee.devhive.domain.user.favorite.entity.Favorite;
import com.devee.devhive.domain.user.type.ActivityStatus;
import com.devee.devhive.domain.user.type.GenderType;
import com.devee.devhive.global.entity.BaseEntity;
import com.devee.devhive.domain.user.type.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String nickName;

  private String name;

  @Enumerated(EnumType.STRING)
  private GenderType gender;

  private String region;

  private String profileImage;

  private String intro;

  @OneToMany(mappedBy = "user")
  private List<UserTechStack> userTechStacks;

  private double rankPoint;

  @Enumerated(EnumType.STRING)
  private ActivityStatus status;

  @OneToMany(mappedBy = "user")
  private List<Favorite> favorites;

  @OneToMany(mappedBy = "user")
  private List<Bookmark> bookmarks;

  @LastModifiedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime modifiedDate;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  private String refreshToken;

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public User update(String name, String profileImage) {
    this.name = name;
    this.profileImage = profileImage;

    return this;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> roles = new HashSet<>();
    roles.add(new SimpleGrantedAuthority(getRoleKey()));
    return roles;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
