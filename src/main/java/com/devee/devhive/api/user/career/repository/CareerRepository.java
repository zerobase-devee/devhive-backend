package com.devee.devhive.api.user.career.repository;

import com.devee.devhive.api.user.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

}
