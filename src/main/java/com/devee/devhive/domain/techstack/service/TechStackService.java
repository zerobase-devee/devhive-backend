package com.devee.devhive.domain.techstack.service;

import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.CreateTechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.global.s3.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TechStackService {

  private final TechStackRepository techStackRepository;
  private final S3Service s3Service;

  public List<TechStack> findAllById(List<Long> techStackIds) {
    return techStackRepository.findAllById(techStackIds);
  }

  public void createTechStack(CreateTechStackDto techStackDto, MultipartFile imageFile) {
    String imageUrl = s3Service.upload(imageFile);

    TechStack techStack = TechStack.builder()
        .name(techStackDto.getName())
        .image(imageUrl)
        .build();

    techStackRepository.save(techStack);
  }
}
