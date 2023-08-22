package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.RelatedUrlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmUserDto {

    private Long userId;
    private String nickName;
    private String relatedUserUrl;

    public static AlarmUserDto of(User user, RelatedUrlType urlType) {
        String relatedUserUrl = "";
        Long userId = user.getId();
        if (urlType == RelatedUrlType.USER_INFO) {
            relatedUserUrl = urlType.getValue() + userId;
        }
        return AlarmUserDto.builder()
            .userId(userId)
            .nickName(user.getNickName())
            .relatedUserUrl(relatedUserUrl)
            .build();
    }
}
