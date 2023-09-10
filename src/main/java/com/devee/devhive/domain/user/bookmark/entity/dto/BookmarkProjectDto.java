package com.devee.devhive.domain.user.bookmark.entity.dto;

import com.devee.devhive.domain.user.bookmark.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkProjectDto {

    private Long bookmarkId;
    private Long projectId;
    private String title;

    public static BookmarkProjectDto from(Bookmark bookmark) {
        return BookmarkProjectDto.builder()
            .bookmarkId(bookmark.getId())
            .projectId(bookmark.getProject().getId())
            .title(bookmark.getProject().getTitle())
            .build();
    }
}
