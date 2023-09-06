package com.devee.devhive.domain.user.career.controller;

import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.service.CareerService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.career.entity.form.UpdateCareerForm;
import com.devee.devhive.domain.user.service.UserService;
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
public class CareerController {
  private final UserService userService;
  private final CareerService careerService;

  // 내 기타 정보 수정 (기술스택, 경력)
  @PutMapping("/my-profile/careers")
  public void updateEtcInfo(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody UpdateCareerForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    careerService.updateCareers(user, form.getCareerDtoList());
  }

  // 유저 경력 조회
  @GetMapping("/{userId}/careers")
  public ResponseEntity<List<CareerDto>> getUserCareers(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(careerService.getUserCareers(userId).stream()
        .map(CareerDto::from)
        .collect(Collectors.toList())
    );
  }
}
