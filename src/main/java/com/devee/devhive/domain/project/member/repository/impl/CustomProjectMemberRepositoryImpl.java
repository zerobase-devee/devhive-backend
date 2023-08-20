package com.devee.devhive.domain.project.member.repository.impl;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.entity.QProjectMember;
import com.devee.devhive.domain.project.member.repository.custom.CustomProjectMemberRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomProjectMemberRepositoryImpl implements CustomProjectMemberRepository {

    private final JPAQueryFactory queryFactory;
    private final QProjectMember qProjectMember = QProjectMember.projectMember;

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
}
