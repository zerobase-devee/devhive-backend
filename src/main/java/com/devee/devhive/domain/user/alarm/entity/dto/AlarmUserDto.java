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
        Long userId = user.getId();
        String url = urlType.getValue();
        String relatedUserUrl = urlType == RelatedUrlType.USER_INFO ? String.format(url, userId) : url;

        return AlarmUserDto.builder()
            .userId(userId)
            .nickName(user.getNickName())
            .relatedUserUrl(relatedUserUrl)
            .build();
    }
}
