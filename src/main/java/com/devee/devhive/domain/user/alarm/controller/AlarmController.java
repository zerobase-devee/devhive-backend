package com.devee.devhive.domain.user.alarm.controller;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmDto;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmUserDto;
import com.devee.devhive.domain.user.alarm.service.AlarmService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.entity.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ALARM API", description = "알람 API")
public class AlarmController {

    private final AlarmService alarmService;
    private final UserService userService;

    // 알람 sse 구독
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "알람 구독")
    public ResponseEntity<SseEmitter> subscribe(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
        return ResponseEntity.ok(alarmService.subscribe(userId, lastEventId));
    }

    // 내 알림 목록 조회
    @GetMapping
    @Operation(summary = "내 알람 목록 조회")
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
        Long args = alarm.getArgs();
        if (args != null) {
            User targetUser = userService.getUserById(alarm.getArgs());
            alarmDto.setUserDto(AlarmUserDto.from(targetUser));
        }
        return alarmDto;
    }

    @DeleteMapping("/{alarmId}")
    @Operation(summary = "알람 삭제")
    public void delete(
        @AuthenticationPrincipal PrincipalDetails principal,
        @PathVariable("alarmId") Long alarmId
    ) {
        User user = userService.getUserByEmail(principal.getEmail());
        alarmService.delete(user.getId(), alarmId);
    }
}
