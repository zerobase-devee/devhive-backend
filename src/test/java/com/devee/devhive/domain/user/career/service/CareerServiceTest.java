package com.devee.devhive.domain.user.career.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import com.devee.devhive.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
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
  void testUpdateCareers() {
    // Given
    User user = User.builder().id(1L).build();

    CareerDto careerDto = CareerDto.builder()
        .company("ABC Company")
        .position("Developer")
        .startDate(LocalDateTime.of(2022,1,1,0,0,0))
        .endDate(LocalDateTime.of(2023,1,1,0,0,0))
        .build();
    List<CareerDto> newCareers = List.of(careerDto);

    List<Career> existingCareers = List.of(Career.builder()
        .user(user)
        .company("XYZ Company")
        .position("Designer")
        .build());

    when(careerRepository.findAllByUserIdOrderByStartDateAsc(user.getId())).thenReturn(existingCareers);
    // When
    careerService.updateCareers(user, newCareers);

    // Then
    verify(careerRepository, times(1)).deleteAll(existingCareers);
    verify(careerRepository, times(1)).saveAll(anyList());
  }
}