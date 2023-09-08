package com.devee.devhive.domain.user.career.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.form.CareerForm;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CareerServiceTest {

  @InjectMocks
  private CareerService careerService;

  @Mock
  private CareerRepository careerRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("유저 경력 업데이트 - 성공")
  void testUpdateCareer() {
    // Given
    Career career = Career.builder()
        .id(3L)
        .company("Old Company")
        .position("ceo")
        .startDate(LocalDateTime.of(2020,1,1,0,0))
        .endDate(null).build();
    CareerForm form = CareerForm.builder()
        .company("New Company")
        .position("New Position")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusYears(1))
        .build();
    when(careerRepository.save(career)).thenReturn(career);
    // When
    Career updatedCareer = careerService.update(career, form);
    // Then
    assertEquals("New Company", updatedCareer.getCompany());
    assertEquals("New Position", updatedCareer.getPosition());
    assertEquals(form.getStartDate(), updatedCareer.getStartDate());
    assertEquals(form.getEndDate(), updatedCareer.getEndDate());
  }
}