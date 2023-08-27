package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.user.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.career.entity.dto.CareerDto;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoDto {

    private Long userId;
    private String email;
    private String region;
    private String nickName;
    private boolean isLocalLogin;
    private String profileImage;
    private String intro;
    private List<TechStackDto> techStacks;
    private List<CareerDto> careers;
    private List<BadgeDto> badges;
    private List<ProjectHistoryDto> projectHistories;
    private int hiveLevel;
    private int exitNum;

    public static MyInfoDto of(User user, UserInformationDto informationDto) {
        return MyInfoDto.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .region(user.getRegion())
            .nickName(user.getNickName())
            .isLocalLogin(user.getProviderType() == ProviderType.LOCAL)
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
