package com.devee.devhive.domain.techstack.service;

import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;

    public List<TechStack> findAllById(List<Long> techStackIds) {
        return techStackRepository.findAllById(techStackIds);
    }
}
