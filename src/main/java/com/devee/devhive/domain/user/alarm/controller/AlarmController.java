package com.devee.devhive.domain.user.alarm.controller;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmDto;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmUserDto;
import com.devee.devhive.domain.user.alarm.service.AlarmService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.domain.user.type.AlarmContent;
import com.devee.devhive.domain.user.type.RelatedUrlType;
import com.devee.devhive.global.entity.PrincipalDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/users/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final UserService userService;

    // 알람 sse 구독
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
        User user = userService.getUserByEmail(principalDetails.getEmail());

        return ResponseEntity.ok(
            alarmService.subscribe(user.getId(), lastEventId)
        );
    }

    // 내 알림 목록 조회
    @GetMapping
    public ResponseEntity<List<AlarmDto>> alarms(@AuthenticationPrincipal PrincipalDetails principal) {
        User user = userService.getUserByEmail(principal.getEmail());
        List<Alarm> alarms = alarmService.getAlarms(user.getId());
        List<AlarmDto> alarmDtoList = alarms.stream()
            .map(this::mapToAlarmDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(alarmDtoList);
    }

    private AlarmDto mapToAlarmDto(Alarm alarm) {
        AlarmDto alarmDto = AlarmDto.from(alarm);
        AlarmContent content = alarm.getContent();

        if (content == AlarmContent.FAVORITE_USER ||
            content == AlarmContent.VOTE_RESULT_EXIT ||
            content == AlarmContent.EXIT_LEADER_DELETE_PROJECT ||
            content == AlarmContent.EXIT_VOTE) {
            User targetUser = userService.getUserById(alarm.getArgs());
            alarmDto.setUserDto(AlarmUserDto.of(targetUser, RelatedUrlType.USER_INFO));
        } else {
            alarmDto.setUserDto(AlarmUserDto.of(alarm.getUser(), RelatedUrlType.MY_INFO));
        }

        return alarmDto;
    }

    @DeleteMapping("/{alarmId}")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principal,
        @PathVariable("alarmId") Long alarmId
    ) {
        User user = userService.getUserByEmail(principal.getEmail());
        alarmService.delete(user.getId(), alarmId);
    }
}
