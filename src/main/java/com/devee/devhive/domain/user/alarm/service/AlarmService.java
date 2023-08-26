package com.devee.devhive.domain.user.alarm.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_ALARM;

import com.devee.devhive.domain.user.alarm.entity.Alarm;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.alarm.repository.AlarmRepository;
import com.devee.devhive.domain.user.alarm.repository.emitter.EmitterRepository;
import com.devee.devhive.global.exception.CustomException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  private final AlarmRepository alarmRepository;
  private final EmitterRepository emitterRepository;

  public SseEmitter subscribe(Long userId, String lastEventId) {
    String emitterId = makeTimeIncludeId(userId);
    SseEmitter emitter = emitterRepository.save(emitterId,
        new SseEmitter(DEFAULT_TIMEOUT));
    emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

    // 503 에러를 방지하기 위한 더미 이벤트 전송
    String eventId = makeTimeIncludeId(userId);
    sendAlarm(emitter, eventId, emitterId,
        "EventStream Created. [userId=" + userId + "]");

    // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
    if (hasLostData(lastEventId)) {
      sendLostData(lastEventId, userId, emitterId, emitter);
    }

    return emitter;
  }

  // 알림 저장하고 클라이언트에게 전송
  public void send(AlarmForm form) {
    Alarm saveAlarm = alarmRepository.save(Alarm.from(form));
    log.info("알림 저장 완료");
    Long userId = form.getReceiverUser().getId();
    String eventId = makeTimeIncludeId(userId);
    Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(userId + "_");
    emitters.forEach(
        (key, emitter) -> {
          emitterRepository.saveEventCache(key, saveAlarm.getId());
          sendAlarm(emitter, eventId, key, "newAlarm");
        }
    );
  }

  private String makeTimeIncludeId(Long userId) {
    return userId + "_" + System.currentTimeMillis();
  }

  // 클라이언트에게 알림 전달하는 부분
  private void sendAlarm(SseEmitter emitter, String eventId, String emitterId,
      Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(eventId)
          .data(data));
      log.info("알림 전송 완료");
    } catch (IOException exception) {
      emitterRepository.deleteById(emitterId);
      log.error("SSE 연결이 올바르지 않습니다. 해당 userId={}", eventId);
    }
  }

  private boolean hasLostData(String lastEventId) {
    return !lastEventId.isEmpty();
  }

  private void sendLostData(String lastEventId, Long userId, String emitterId,
      SseEmitter emitter) {
    Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(userId + "_");
    eventCaches.entrySet().stream()
        .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
        .forEach(entry -> sendAlarm(emitter, entry.getKey(), emitterId,
            entry.getValue()));
  }

  public List<Alarm> getAlarms(Long userId) {
    return alarmRepository.findAllByUserIdOrderByCreatedDateDesc(userId);
  }

  public void delete(Long userId, Long alarmId) {
    Alarm alarm = alarmRepository.findById(alarmId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_ALARM));
    if (Objects.equals(userId, alarm.getUser().getId())) {
      alarmRepository.delete(alarm);
    }
  }
}
