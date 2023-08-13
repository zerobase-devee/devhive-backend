package com.devee.devhive.domain.project.review.evaluation.repository;

import com.devee.devhive.domain.project.review.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

}
