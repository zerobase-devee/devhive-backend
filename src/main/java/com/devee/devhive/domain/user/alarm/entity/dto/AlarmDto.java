package com.devee.devhive.domain.user.alarm.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDto {

    private Long alarmId;
    private AlarmUserDto userDto;
    private AlarmProjectDto projectDto;
    private String content;

}
