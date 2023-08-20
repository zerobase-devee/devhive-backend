package com.devee.devhive.domain.user.career.service;

import com.devee.devhive.domain.user.career.entity.Career;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.career.repository.CareerRepository;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;

    public List<Career> getUserCareers(Long userId) {
        return careerRepository.findAllByUserIdOrderByStartDateAsc(userId);
    }

    @Transactional
    public void updateCareers(User user, List<CareerDto> newCareers) {
        List<Career> existingCareers = getUserCareers(user.getId());

        // 기존 유저경력 비었다면 요청된 경력 바로 저장
        if (existingCareers.isEmpty()) {
            careerRepository.saveAll(newCareers.stream()
                .map(careerDto -> Career.of(user, careerDto))
                .collect(Collectors.toList()));
        } else if (newCareers.isEmpty()) {
            // 요청 경력이 비었다면 기존 유저경력 모두 삭제
            careerRepository.deleteAll(existingCareers);
        } else {
            // 기존 유저경력 삭제할거 삭제, 추가할거 추가 저장
            List<Career> careersToDelete = existingCareers.stream()
                .filter(career -> newCareers.stream()
                    .noneMatch(careerDto ->
                        careerDto.equals(CareerDto.from(career))))
                .toList();

            if (!careersToDelete.isEmpty()) {
                careerRepository.deleteAll(careersToDelete);
            }

            List<CareerDto> careersToAdd = newCareers.stream()
                .filter(careerDto -> existingCareers.stream()
                    .noneMatch(existingCareer ->
                        CareerDto.from(existingCareer).equals(careerDto)))
                .toList();

            if (!careersToAdd.isEmpty()) {
                careerRepository.saveAll(newCareers.stream()
                    .map(careerDto -> Career.of(user, careerDto))
                    .collect(Collectors.toList()));
            }
        }
    }

}
