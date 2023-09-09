package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.ApplyStatus;
import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.entity.User;
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
public class ProjectInfoDto {
  private ProjectStatus status;
  private String projectTitle;
  private LocalDateTime createDate;
  private LocalDateTime modifiedDate;
  private int viewCount;
  private RecruitmentType recruitmentType;
  private String region;
  private DevelopmentType developmentType;
  private int recruitmentNum;
  private LocalDateTime deadline;
  private String projectName;
  private List<TechStackDto> techStacks;
  private String content;
  // 작성자
  private SimpleUserDto writerInfo;
  // 프로젝트 멤버리스트
  private List<SimpleUserDto> projectMembers;
  // 본인
  private SimpleUserDto userInfo;
  private boolean isBookmark;
  private ApplyStatus applyStatus;

  public static ProjectInfoDto of(Project project, List<TechStackDto> techStacks,
      List<SimpleUserDto> projectMembers, User user, boolean isBookmark, ApplyStatus applyStatus) {
    return ProjectInfoDto.builder()
        .status(project.getStatus())
        .projectTitle(project.getTitle())
        .createDate(project.getCreatedDate())
        .modifiedDate(project.getModifiedDate())
        .viewCount(project.getViewCount().getCount())
        .recruitmentType(project.getRecruitmentType())
        .region(project.getRegion())
        .developmentType(project.getDevelopmentType())
        .recruitmentNum(project.getTeamSize() - projectMembers.size())
        .deadline(project.getDeadline())
        .projectName(project.getName())
        .content(project.getContent())
        .techStacks(techStacks)
        .writerInfo(SimpleUserDto.from(project.getUser()))
        .projectMembers(projectMembers)
        .userInfo(SimpleUserDto.from(user))
        .isBookmark(isBookmark)
        .applyStatus(applyStatus)
        .build();
  }
}
