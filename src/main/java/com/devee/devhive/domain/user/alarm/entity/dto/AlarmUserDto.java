package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.user.entity.User;
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

    public static AlarmUserDto from(User user) {
        return AlarmUserDto.builder()
            .userId(user.getId())
            .nickName(user.getNickName())
            .build();
    }
}
