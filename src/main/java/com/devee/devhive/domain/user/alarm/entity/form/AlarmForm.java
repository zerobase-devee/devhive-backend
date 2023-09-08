package com.devee.devhive.domain.user.alarm.entity.form;

import com.devee.devhive.domain.project.entity.Project;
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
    private Project project;
    private AlarmContent content;
    private User user;
}
