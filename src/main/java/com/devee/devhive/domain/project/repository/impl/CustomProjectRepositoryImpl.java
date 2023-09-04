// CustomProjectRepositoryImpl.java
package com.devee.devhive.domain.project.repository.impl;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.QProject;
import com.devee.devhive.domain.project.techstack.entity.QProjectTechStack;
import com.devee.devhive.domain.project.repository.custom.CustomProjectRepository;
import com.devee.devhive.domain.project.type.DevelopmentType;
import com.devee.devhive.domain.project.type.RecruitmentType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

  private final JPAQueryFactory queryFactory;

  public CustomProjectRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<Project> getProject(String keyword, DevelopmentType development,
      RecruitmentType recruitment, List<Long> techStackIds, String sort, Pageable pageable) {

    QProject qProject = QProject.project;
    QProjectTechStack qProjectTechStack = QProjectTechStack.projectTechStack;
    BooleanBuilder predicate = new BooleanBuilder();

    if (keyword != null && !keyword.isEmpty()) {
      predicate.and(qProject.title.containsIgnoreCase(keyword)
          .or(qProject.content.containsIgnoreCase(keyword)));
    }

    if (development != null) {
      predicate.and(qProject.developmentType.eq(development));
    }

    if (recruitment != null) {
      predicate.and(qProject.recruitmentType.eq(recruitment));
    }

    if (techStackIds != null && !techStackIds.isEmpty()) {
      predicate.and(qProjectTechStack.techStack.id.in(techStackIds));
    }

    JPAQuery<Project> query = queryFactory.selectFrom(qProject)
        .leftJoin(qProjectTechStack)
        .on(qProjectTechStack.project.eq(qProject))
        .where(predicate)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    if ("asc".equals(sort)) {
      query = query.orderBy(qProject.createdDate.asc());
    } else {
      query = query.orderBy(qProject.createdDate.desc());
    }

    List<Project> projectList = query.fetch();

    return new PageImpl<>(projectList);
  }
}