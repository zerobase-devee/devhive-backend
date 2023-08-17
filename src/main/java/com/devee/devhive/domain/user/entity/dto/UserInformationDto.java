package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformationDto {

    private List<TechStackDto> techStacks;
    private List<CareerDto> careers;
    private List<BadgeDto> badges;
    private List<ProjectHistoryDto> projectHistories;
    private int hiveLevel;
    private int exitNum;

    public static UserInformationDto of(
        List<TechStackDto> techStacks, List<CareerDto> careers, List<BadgeDto> badges,
        List<ProjectHistoryDto> projectHistories, int hiveLevel, int exitNum
    ) {
        return UserInformationDto.builder()
            .techStacks(techStacks)
            .careers(careers)
            .badges(badges)
            .projectHistories(projectHistories)
            .hiveLevel(hiveLevel)
            .exitNum(exitNum)
            .build();
    }
}
