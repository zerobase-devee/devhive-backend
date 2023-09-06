package com.devee.devhive.domain.techstack.repository;

import com.devee.devhive.domain.techstack.entity.TechStack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

  TechStack findByName(String techStackName);

  boolean existsByName(String name);

  List<TechStack> findAllByOrderByNameAsc();
}
