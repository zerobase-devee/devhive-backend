package com.devee.devhive.domain.user.alarm.entity.form;

import com.devee.devhive.domain.user.alarm.entity.dto.AlarmProjectDto;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmUserDto;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.AlarmContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmForm {
    private User receiverUser;
    private AlarmContent content;
    private AlarmUserDto userDto;
    private AlarmProjectDto projectDto;
}
