package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectListDto {

  private Long id;
  private String userNickname;
  private String profileImage;
  private ProjectStatus status;
  private String title;
  private String name;
  private LocalDateTime deadline;
  private DevelopmentType developmentType;
  private RecruitmentType recruitmentType;
  private String region;
  private LocalDateTime createDate;
  private int viewCount;
  private boolean bookmarked;
  private List<TechStackDto> techStackList;
  private List<SimpleUserDto> projectMemberList;

  public static ProjectListDto of(Project project, List<TechStackDto> techStackList,
      List<SimpleUserDto> projectMemberList, boolean bookmarkedProjectIds) {

    return ProjectListDto.builder()
        .id(project.getId())
        .userNickname(project.getUser().getNickName())
        .profileImage(project.getUser().getProfileImage())
        .status(project.getStatus())
        .title(project.getTitle())
        .name(project.getName())
        .deadline(project.getDeadline())
        .developmentType(project.getDevelopmentType())
        .recruitmentType(project.getRecruitmentType())
        .region(project.getRegion())
        .createDate(project.getCreatedDate())
        .viewCount(project.getViewCount())
        .techStackList(techStackList)
        .projectMemberList(projectMemberList)
        .bookmarked(bookmarkedProjectIds)
        .build();
  }
}
