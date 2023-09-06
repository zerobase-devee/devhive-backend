package com.devee.devhive.domain.user.career.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_CAREER;

import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.form.CareerForm;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerService {

  private final CareerRepository careerRepository;

  public Career findByCareerId(Long careerId) {
    return careerRepository.findById(careerId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_CAREER));
  }

  public List<Career> getUserCareers(Long userId) {
    return careerRepository.findAllByUserIdOrderByStartDateAsc(userId);
  }

  public Career create(User user, CareerForm form) {
    return careerRepository.save(Career.of(user, form));
  }

  public void delete(Career career) {
    careerRepository.delete(career);
  }

  public Career update(Career career, CareerForm form) {
    // 회사명 업데이트
    String newCompany = form.getCompany();
    if (!career.getCompany().equals(newCompany)) {
      career.setCompany(newCompany);
    }
    // 직급 업데이트
    String newPosition = form.getPosition();
    if (!career.getPosition().equals(newPosition)) {
      career.setPosition(newPosition);
    }
    // 입사일 업데이트
    LocalDateTime newStartDate = form.getStartDate();
    if (!career.getStartDate().equals(newStartDate)) {
      career.setStartDate(newStartDate);
    }
    // 퇴사일 업데이트
    LocalDateTime newEndDate = form.getEndDate();
    career.setEndDate(newEndDate);

    return careerRepository.save(career);
  }
}
