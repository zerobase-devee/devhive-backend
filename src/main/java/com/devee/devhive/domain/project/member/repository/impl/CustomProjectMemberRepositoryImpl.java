package com.devee.devhive.domain.project.member.repository.impl;

import com.devee.devhive.domain.project.entity.QProject;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.entity.QProjectMember;
import com.devee.devhive.domain.project.member.repository.custom.CustomProjectMemberRepository;
import com.devee.devhive.domain.project.review.entity.QProjectReview;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.dto.ProjectHistoryDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomProjectMemberRepositoryImpl implements CustomProjectMemberRepository {

    private final JPAQueryFactory queryFactory;
    private final QProjectMember qProjectMember = QProjectMember.projectMember;
    private final QProjectReview qProjectReview = QProjectReview.projectReview;
    private final QProject qProject = QProject.project;

    @Override
    public int countCompletedProjectsByUserId(Long userId) {

        List<ProjectMember> projectMembers = queryFactory
            .selectFrom(qProjectMember)
            .join(qProjectMember.project)
            .where(qProjectMember.user.id.eq(userId)
                .and(qProjectMember.project.status.eq(ProjectStatus.COMPLETE)))
            .fetch();

        return projectMembers.size();
    }

    @Override
    public List<ProjectHistoryDto> getProjectNamesAndAverageReviewScoreByUserId(Long userId) {
        List<Tuple> tupleList = queryFactory
            .select(qProject.name, qProjectReview.totalScore.avg())
            .from(qProjectMember)
            .join(qProjectMember.project, qProject)
            .join(qProjectReview).on(qProject.id.eq(qProjectReview.project.id))
            .where(qProjectMember.user.id.eq(userId)
                .and(qProjectReview.targetUser.id.eq(userId)))  // 리뷰의 대상 유저 필터링
            .groupBy(qProject.name)
            .orderBy(qProject.endDate.desc())  // 종료일자 내림차순 정렬
            .fetch();

        return convertToDtoList(tupleList);
    }

    private List<ProjectHistoryDto> convertToDtoList(List<Tuple> tupleList) {
        List<ProjectHistoryDto> projectHistoryDtoList = new ArrayList<>();

        for (Tuple tuple : tupleList) {
            String projectName = tuple.get(qProject.name);
            Double totalAverageScore = tuple.get(qProjectReview.totalScore.avg());
            // 평균값 소수점 첫째자리까지, 없으면 0.0으로 설정
            totalAverageScore = totalAverageScore != null ? Math.round(totalAverageScore * 10.0) / 10.0 : 0.0;

            projectHistoryDtoList.add(ProjectHistoryDto.of(projectName, totalAverageScore));
        }

        return projectHistoryDtoList;
    }
}
