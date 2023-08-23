package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.domain.user.type.RelatedUrlType;
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
    private AlarmUserDto userDto;
    private AlarmProjectDto projectDto;
    private String content;
    private LocalDateTime createDate;

    public static AlarmDto from(Alarm alarm) {
        AlarmContent alarmContent = alarm.getContent();
        AlarmDto alarmDto = AlarmDto.builder()
            .alarmId(alarm.getId())
            .content(alarmContent.getValue())
            .createDate(alarm.getCreatedDate())
            .build();

        switch (alarmContent) {
            case COMMENT, REPLY, APPLICANT_ACCEPT, APPLICANT_REJECT, FAVORITE_USER, RECOMMEND ->
                alarmDto.setProjectDto(
                    AlarmProjectDto.of(alarm.getProject(), RelatedUrlType.PROJECT_POST));
            case PROJECT_APPLY -> alarmDto.setProjectDto(
                AlarmProjectDto.of(alarm.getProject(), RelatedUrlType.PROJECT_APPLICANTS));
            case REVIEW_REQUEST, REVIEW_RESULT, EXIT_VOTE, EXIT_RESULT -> alarmDto.setProjectDto(
                AlarmProjectDto.of(alarm.getProject(), RelatedUrlType.PROJECT_INFO));
        }

        return alarmDto;
    }
}
