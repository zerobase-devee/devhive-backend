package com.devee.devhive.global.component;

import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AlarmListener {

    private final AlarmService alarmService;

    @TransactionalEventListener
    @Async // 비동기적으로 처리
    public void alarmHandler(AlarmForm form) {
        alarmService.send(form);
    }
}
