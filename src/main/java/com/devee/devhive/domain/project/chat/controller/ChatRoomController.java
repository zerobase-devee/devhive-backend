package com.devee.devhive.domain.project.chat.controller;

import static com.devee.devhive.global.exception.ErrorCode.ALREADY_CREATE_CHATROOM;
import static com.devee.devhive.global.exception.ErrorCode.ALREADY_ENTER_CHATROOM;
import static com.devee.devhive.global.exception.ErrorCode.NOT_YOUR_PROJECT;

import com.devee.devhive.domain.project.chat.entity.ProjectChatRoom;
import com.devee.devhive.domain.project.chat.entity.dto.ChatRoomDto;
import com.devee.devhive.domain.project.chat.entity.dto.ChatRoomForm;
import com.devee.devhive.domain.project.chat.service.ChatMemberService;
import com.devee.devhive.domain.project.chat.service.ChatRoomService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final ChatMemberService chatMemberService;
  private final ProjectService projectService;
  private final ProjectMemberService projectMemberService;

  @PostMapping("/room/{projectId}")
  public ResponseEntity<ChatRoomDto> createChatRoom(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long projectId,
      @RequestBody ChatRoomForm chatRoomForm
  ) {
    User user = principalDetails.getUser();
    String title = chatRoomForm.getTitle();
    Project project = projectService.findById(projectId);

    if (!projectMemberService.isMemberofProject(projectId, user.getId())) {
      throw new CustomException(NOT_YOUR_PROJECT);
    }

    if (!chatRoomService.existsRoomByProjectId(projectId)) {
      throw new CustomException(ALREADY_CREATE_CHATROOM);
    }

    ProjectChatRoom newChatRoom = chatRoomService.createChatRoom(project, title);

    return ResponseEntity.ok(ChatRoomDto.from(newChatRoom));
  }

  @PostMapping("{roomId}")
  public ResponseEntity<String> enterChatRoom(
      @AuthenticationPrincipal PrincipalDetails principalDetails,
      @PathVariable Long roomId
  ) {
    User user = principalDetails.getUser();
    ProjectChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
    Long projectId = chatRoom.getProject().getId();

    if (!projectMemberService.isMemberofProject(projectId, user.getId())) {
      throw new CustomException(NOT_YOUR_PROJECT);
    }

    if (chatMemberService.isMemberOfChat(roomId, user.getId())) {
      throw new CustomException(ALREADY_ENTER_CHATROOM);
    }

    return ResponseEntity.ok(chatMemberService.enterChatRoom(chatRoom, user));
  }
}
