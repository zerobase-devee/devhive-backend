package com.devee.devhive.api.techstack.repository;

import com.devee.devhive.api.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {

}
