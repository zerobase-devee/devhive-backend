package com.devee.devhive.domain.project.controller;

import static com.devee.devhive.global.exception.ErrorCode.PROJECT_CANNOT_DELETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.apply.service.ProjectApplyService;
import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.ProjectInfoDto;
import com.devee.devhive.domain.project.entity.dto.ProjectListDto;
import com.devee.devhive.domain.project.entity.dto.SearchProjectDto;
import com.devee.devhive.domain.project.entity.dto.SimpleProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.techstack.service.ProjectTechStackService;
import com.devee.devhive.domain.project.type.ApplyStatus;
import com.devee.devhive.domain.project.views.service.ViewCountService;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.bookmark.service.BookmarkService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.entity.dto.SimpleUserDto;
import com.devee.devhive.domain.user.favorite.service.FavoriteService;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.techstack.service.UserTechStackService;
import com.devee.devhive.domain.user.type.Role;
import com.devee.devhive.global.entity.PrincipalDetails;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@Tag(name = "PROJECT API", description = "프로젝트 API")
public class ProjectController {

  private final UserService userService;
  private final ProjectService projectService;
  private final CommentService commentService;
  private final ReplyService replyService;
  private final ProjectTechStackService projectTechStackService;
  private final ProjectMemberService projectMemberService;
  private final FavoriteService favoriteService;
  private final UserTechStackService userTechStackService;
  private final BookmarkService bookmarkService;
  private final ProjectApplyService projectApplyService;
  private final S3Service s3Service;
  private final ViewCountService viewCountService;

  // 프로젝트 작성
  @PostMapping
  @Operation(summary = "프로젝트 생성")
  public ResponseEntity<SimpleProjectDto> createProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody @Valid CreateProjectDto createProjectDto
  ) {
    User user = userService.getUserByEmail(principal.getEmail());

    Project project = projectService.createProject(createProjectDto, user);
    List<TechStackDto> techStacks = createProjectDto.getTechStacks();
    projectTechStackService.createProjectTechStacks(project, techStacks);
    projectMemberService.saveProjectLeader(user, project);
    // 관심유저로 등록한 유저들에게 알림 발행
    favoriteService.favoriteUserUploadAlarmOfProject(user, user.getId(), project);
    // 프로젝트에 등록되는 기술, 지역이 포함된 유저들에게 알림 발행
    userTechStackService.recommendAlarmOfProject(project, techStacks);
    return ResponseEntity.ok(SimpleProjectDto.from(project));
  }

  // 상태변경
  @PutMapping("/{projectId}/status")
  @Operation(summary = "프로젝트 상태 변경", description = "프로젝트 고유 ID로 프로젝트 상태 변경 - 글 작성자만 변경 가능(모집중, 모집 완료, 재모집, 완료)")
  public void updateProjectStatus(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId, @RequestBody UpdateProjectStatusDto statusDto
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    List<ProjectMember> members = projectMemberService.getProjectMemberByProjectId(projectId);
    projectService.updateProjectStatusAndAlarmToMembers(user, projectId, statusDto, members);
  }

  // 프로젝트 수정
  @PutMapping("/{projectId}")
  @Operation(summary = "프로젝트 수정", description = "프로젝트 고유 ID로 프로젝트 수정 - 글 작성자만 수정 가능")
  public void updateProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId, @RequestBody @Valid UpdateProjectDto updateProjectDto
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Project project = projectService.updateProject(user, projectId, updateProjectDto);
    projectTechStackService.updateProjectTechStacks(project, updateProjectDto.getTechStacks());
  }

  // 프로젝트 삭제
  @DeleteMapping("/{projectId}")
  @Operation(summary = "프로젝트 상태 변경", description = "프로젝트 고유 ID로 프로젝트 삭제 - 글 작성자만 삭제 가능")
  public void deleteProject(
      @AuthenticationPrincipal PrincipalDetails principal, @PathVariable Long projectId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Project project = projectService.findById(projectId);
    if (!Objects.equals(project.getUser().getId(), user.getId())) {
      throw new CustomException(PROJECT_CANNOT_DELETED);
    }

    List<Long> commentIdList = commentService.deleteCommentsByProjectId(projectId);
    replyService.deleteRepliesByCommentList(commentIdList);
    projectTechStackService.deleteProjectTechStacksByProjectId(projectId);
    projectMemberService.deleteProjectMembers(projectId);
    projectService.deleteProject(project);
  }

  @PostMapping("/list")
  @Operation(summary = "프로젝트 목록 조회", description = "키워드(제목,내용), 백/프론트, 온/오프라인, 테크스택 고유 ID 리스트")
  public ResponseEntity<Page<ProjectListDto>> getProjects(
      @RequestBody(required = false) SearchProjectDto searchRequest,
      @RequestParam(defaultValue = "desc") String sort, Pageable pageable
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = getLoggedInUser(authentication);

    Page<Project> projectPage = projectService.getProject(searchRequest, sort, pageable);

    Page<ProjectListDto> projectListDtoPage = projectPage.map(project -> {
      List<TechStackDto> techStackDtoList = projectTechStackService.getProjectTechStacksByProject(
              project)
          .stream()
          .map(projectTechStack -> TechStackDto.from(projectTechStack.getTechStack()))
          .collect(Collectors.toList());

      List<SimpleUserDto> projectMemberDtoList = projectMemberService.getProjectMemberByProjectId(
              project.getId())
          .stream()
          .map(projectMember -> SimpleUserDto.from(projectMember.getUser()))
          .collect(Collectors.toList());

      boolean bookmarked = isLoggedInUserBookmark(user, project.getId());

      return ProjectListDto.of(project, techStackDtoList, projectMemberDtoList, bookmarked);
    });

    return ResponseEntity.ok(projectListDtoPage);
  }

  // 이미지 업로드 후 url 얻는 api
  @PostMapping("/image")
  public String getImageUrl(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestPart(value = "image", required = false) MultipartFile multipartFile
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    if (!(user.getRole() == Role.ADMIN || user.getRole() == Role.USER)) {
      throw new CustomException(UNAUTHORIZED);
    }
    return s3Service.upload(multipartFile);
  }

  // 프로젝트 상세 조회
  @GetMapping("/{projectId}")
  @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 고유 ID로 프로젝트 조회")
  public ResponseEntity<ProjectInfoDto> getProjectInfo(@PathVariable("projectId") Long projectId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Project project = projectService.findById(projectId);
    viewCountService.incrementViewCount(project);
    List<TechStackDto> techStacks = getTechStacks(projectId);

    List<SimpleUserDto> projectMembers = getProjectMembers(projectId);

    User loggedInUser = getLoggedInUser(authentication);
    boolean isBookmark = isLoggedInUserBookmark(loggedInUser, projectId);
    ApplyStatus applyStatus = getApplyStatus(loggedInUser, project);

    return ResponseEntity.ok(ProjectInfoDto.of(
        project, techStacks, projectMembers, loggedInUser, isBookmark, applyStatus)
    );
  }

  private List<TechStackDto> getTechStacks(Long projectId) {
    return projectTechStackService.getTechStacks(projectId).stream()
        .map(projectTechStack -> TechStackDto.from(projectTechStack.getTechStack()))
        .toList();
  }

  private List<SimpleUserDto> getProjectMembers(Long projectId) {
    return projectMemberService.getProjectMemberByProjectId(projectId).stream()
        .map(projectMember -> SimpleUserDto.from(projectMember.getUser()))
        .toList();
  }

  private User getLoggedInUser(Authentication authentication) {
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      PrincipalDetails details = (PrincipalDetails) authentication.getPrincipal();
      return userService.getUserByEmail(details.getEmail());
    }
    return null;
  }

  private boolean isLoggedInUserBookmark(User loggedInUser, Long projectId) {
    return loggedInUser != null && bookmarkService.isBookmark(loggedInUser.getId(), projectId);
  }

  private ApplyStatus getApplyStatus(User loggedInUser, Project project) {
    if (loggedInUser != null) {
      return projectApplyService.getApplicationStatus(loggedInUser, project);
    }
    return null;
  }
}
