package com.devee.devhive.domain.project.entity;

import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Project extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private String title;
  private String content;
  private String name;
  private int teamSize;
  private int viewCount;

  @Enumerated(EnumType.STRING)
  private ProjectStatus status;

  @Enumerated(EnumType.STRING)
  private RecruitmentType recruitmentType;

  private String region;

  @Enumerated(EnumType.STRING)
  private DevelopmentType developmentType;

  private LocalDateTime startDate; // 프로젝트 시작일자
  private LocalDateTime endDate;   // 프로젝트 종료일자
  private LocalDateTime deadline;  // 모집 마감 종료일자

  @LastModifiedDate
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime modifiedDate;
}
