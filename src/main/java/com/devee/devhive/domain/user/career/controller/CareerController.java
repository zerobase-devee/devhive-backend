package com.devee.devhive.domain.user.career.controller;

import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.entity.form.CareerForm;
import com.devee.devhive.domain.user.career.service.CareerService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.devee.devhive.global.exception.CustomException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "CAREER API", description = "경력 API")
public class CareerController {
  private final UserService userService;
  private final CareerService careerService;

  // 경력 등록
  @PostMapping("/my-profile/careers")
  @Operation(summary = "내 경력 등록)")
  public ResponseEntity<CareerDto> createCareer(
      @AuthenticationPrincipal PrincipalDetails principal, @RequestBody CareerForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Career saveCareer = careerService.create(user, form);

    return ResponseEntity.ok(CareerDto.from(saveCareer));
  }

  // 경력 수정
  @PutMapping("/my-profile/careers/{careerId}")
  @Operation(summary = "내 경력 수정")
  public ResponseEntity<CareerDto> updateCareer(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("careerId") Long careerId,
      @RequestBody CareerForm form
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Career career = careerService.findByCareerId(careerId);
    if (!Objects.equals(user.getId(), career.getUser().getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    Career update = careerService.update(career, form);
    return ResponseEntity.ok(CareerDto.from(update));
  }

  // 경력 삭제
  @DeleteMapping("/my-profile/careers/{careerId}")
  @Operation(summary = "내 경력 삭제")
  public void deleteCareer(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable("careerId") Long careerId
  ) {
    User user = userService.getUserByEmail(principal.getEmail());
    Career career = careerService.findByCareerId(careerId);
    if (!Objects.equals(user.getId(), career.getUser().getId())) {
      throw new CustomException(UNAUTHORIZED);
    }
    careerService.delete(career);
  }

  // 유저 경력 조회
  @GetMapping("/{userId}/careers")
  @Operation(summary = "유저 경력 조회")
  public ResponseEntity<List<CareerDto>> getUserCareers(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(careerService.getUserCareers(userId).stream()
        .map(CareerDto::from)
        .collect(Collectors.toList())
    );
  }
}
