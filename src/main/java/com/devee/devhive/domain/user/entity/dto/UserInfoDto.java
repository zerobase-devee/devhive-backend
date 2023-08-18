package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private Long userId;
    private boolean isFavorite;
    private String region;
    private String nickName;
    private String profileImage;
    private String intro;
    private List<TechStackDto> techStacks;
    private List<CareerDto> careers;
    private List<BadgeDto> badges;
    private List<ProjectHistoryDto> projectHistories;
    private int hiveLevel;
    private int exitNum;

    public static UserInfoDto of(
        User user, UserInformationDto informationDto, boolean isFavorite
    ) {
        return UserInfoDto.builder()
            .userId(user.getId())
            .isFavorite(isFavorite)
            .region(user.getRegion())
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .intro(user.getIntro())
            .techStacks(informationDto.getTechStacks())
            .careers(informationDto.getCareers())
            .badges(informationDto.getBadges())
            .projectHistories(informationDto.getProjectHistories())
            .hiveLevel(informationDto.getHiveLevel())
            .exitNum(informationDto.getExitNum())
            .build();
    }
}
