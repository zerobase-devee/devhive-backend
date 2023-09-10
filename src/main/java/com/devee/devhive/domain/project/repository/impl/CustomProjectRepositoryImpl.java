package com.devee.devhive.domain.project.repository.impl;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.QProject;
import com.devee.devhive.domain.project.repository.custom.CustomProjectRepository;
import com.devee.devhive.domain.project.techstack.entity.QProjectTechStack;
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
      if (development == DevelopmentType.ALL) {
        predicate.and(qProject.developmentType.in(DevelopmentType.getAllDevelopmentTypes()));
      } else {
        predicate.and(qProject.developmentType.eq(development));
      }
    }

    if (recruitment != null) {
      if (recruitment == RecruitmentType.ALL) {
        predicate.and(qProject.recruitmentType.in(RecruitmentType.getAllRecruitmentTypes()));
      } else {
        predicate.and(qProject.recruitmentType.eq(recruitment));
      }
    }

    if (techStackIds != null && !techStackIds.isEmpty()) {
      predicate.and(qProjectTechStack.techStack.id.in(techStackIds));
    }

    JPAQuery<Project> query = queryFactory.selectFrom(qProject)
        .leftJoin(qProjectTechStack)
        .on(qProjectTechStack.project.eq(qProject))
        .where(predicate);

    if ("asc".equals(sort)) {
      query = query.orderBy(qProject.createdDate.asc());
    } else if ("desc".equals(sort)) {
      query = query.orderBy(qProject.createdDate.desc());
    } else if ("view".equals(sort)) {
      query = query.orderBy(qProject.viewCount.count.desc());
    } else {
      query = query.orderBy(qProject.createdDate.desc());
    }

    // 중복된 결과를 제거하기 위해 distinct()를 사용
    query = query.distinct();

    long totalItems = query.fetchCount();

    // 페이지 크기와 오프셋 설정
    query = query.offset(pageable.getPageNumber()).limit(pageable.getPageSize());

    List<Project> projectList = query.fetch();

    // 페이지 객체 생성 시 총 아이템 수 제공
    return new PageImpl<>(projectList, pageable, totalItems);
  }
}