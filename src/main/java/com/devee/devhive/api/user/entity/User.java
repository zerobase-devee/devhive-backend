package com.devee.devhive.api.user.entity;

import com.devee.devhive.global.entity.BaseEntity;
import com.devee.devhive.api.user.bookmark.entity.Bookmark;
import com.devee.devhive.api.user.favorite.entity.Favorite;
import com.devee.devhive.api.user.type.ActivityStatus;
import com.devee.devhive.api.user.type.GenderType;
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
import java.util.List;
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
public class User extends BaseEntity{
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
}
