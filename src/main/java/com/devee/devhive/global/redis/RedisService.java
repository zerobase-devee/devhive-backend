package com.devee.devhive.global.redis;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {

  private final StringRedisTemplate template;
  private final RedisTemplate<String, String> lockRedisTemplate;

  public String getData(String key) {
    ValueOperations<String, String> valueOperations = template.opsForValue();
    return valueOperations.get(key);
  }

  public boolean existData(String key) {
    return Boolean.TRUE.equals(template.hasKey(key));
  }

  public void setDataExpire(String key, String value, long duration) {
    ValueOperations<String, String> valueOperations = template.opsForValue();
    Duration expireDuration = Duration.ofSeconds(duration);
    valueOperations.set(key, value, expireDuration);
  }

  // 닉네임 락
  public boolean getLock(String key, long timeoutInSeconds) {
    ValueOperations<String, String> ops = lockRedisTemplate.opsForValue();
    Boolean locked = ops.setIfAbsent(key, "locked", timeoutInSeconds, TimeUnit.SECONDS);
    return locked != null && locked;
  }
  // 닉네임 락 해제
  public void unLock(String key) {
    lockRedisTemplate.delete(key);
  }
}
