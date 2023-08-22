package com.devee.devhive.domain.techstack.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_TECH_STACK;

import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.CreateTechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.s3.S3Service;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

  public void deleteTechStack(Long techStackId) {
    TechStack techStack = techStackRepository.findById(techStackId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_TECH_STACK));
    String imageUrl = URLDecoder.decode(techStack.getImage(), StandardCharsets.UTF_8);
    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

    s3Service.delete(filename);

    techStackRepository.delete(techStack);
  }
}
