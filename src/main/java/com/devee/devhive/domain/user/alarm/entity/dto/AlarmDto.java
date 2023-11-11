package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import com.devee.devhive.domain.user.type.AlarmContent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDto {

    private Long alarmId;
    private Long projectId;
    private String projectName;
    private AlarmUserDto userDto;
    private AlarmContent content;
    private LocalDateTime createDate;

    public static AlarmDto from(Alarm alarm) {
        return AlarmDto.builder()
            .alarmId(alarm.getId())
            .projectId(alarm.getProjectId())
            .projectName(alarm.getProjectName())
            .content(alarm.getContent())
            .createDate(alarm.getCreatedDate())
            .build();
    }
}
