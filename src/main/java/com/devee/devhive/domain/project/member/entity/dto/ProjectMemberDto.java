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

    private boolean isLeader;
    private Long userId;
    private String nickName;
    private String profileImage;

    public static ProjectMemberDto from(ProjectMember member) {
        return ProjectMemberDto.builder()
            .isLeader(member.isLeader())
            .userId(member.getUser().getId())
            .nickName(member.getUser().getNickName())
            .profileImage(member.getUser().getProfileImage())
            .build();
    }
}
