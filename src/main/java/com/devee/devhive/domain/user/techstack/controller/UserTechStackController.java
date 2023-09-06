package com.devee.devhive.domain.user.techstack.controller;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.techstack.entity.form.UpdateTechStackForm;
import com.devee.devhive.domain.user.techstack.service.UserTechStackService;
import com.devee.devhive.global.entity.PrincipalDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserTechStackController {

  private final UserService userService;
  private final UserTechStackService userTechStackService;

  // 내 기술스택 수정
  @PutMapping("/my-profile/tech-stacks")
  public void updateUserTechStacks(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody UpdateTechStackForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    userTechStackService.updateTechStacks(user, form.getTechStacks());
  }

  // 유저 기술 스택 조회
  @GetMapping("/{userId}/tech-stacks")
  public ResponseEntity<List<TechStackDto>> getUserTechStacks(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(userTechStackService.getUserTechStacks(userId).stream()
        .map(userTechStack -> TechStackDto.from(userTechStack.getTechStack()))
        .collect(Collectors.toList())
    );
  }
}
