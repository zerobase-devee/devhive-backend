package com.devee.devhive.domain.project.member.entity.dto;

import com.devee.devhive.domain.project.member.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDto {

    private Long userId;
    private String nickName;
    private String profileImage;
    private boolean isReview;

    public static ProjectMemberDto of(ProjectMember member, boolean isReview) {
        return ProjectMemberDto.builder()
            .userId(member.getUser().getId())
            .nickName(member.getUser().getNickName())
            .profileImage(member.getUser().getProfileImage())
            .isReview(isReview)
            .build();
    }
}
