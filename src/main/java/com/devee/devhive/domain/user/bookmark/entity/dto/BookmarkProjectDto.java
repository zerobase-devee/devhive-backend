package com.devee.devhive.domain.user.bookmark.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkProjectDto {

    private Long projectId;
    private String title;

    public static BookmarkProjectDto from(Project project) {
        return BookmarkProjectDto.builder()
            .projectId(project.getId())
            .title(project.getTitle())
            .build();
    }
}
