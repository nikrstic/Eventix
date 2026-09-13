package com.eventix.ticket.infrastructure.adapter.outbound.redis;

import com.eventix.ticket.application.port.outbound.SeatLockPort;
import com.eventix.ticket.domain.model.SeatId;
import com.eventix.ticket.domain.model.UserId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

@Component
public class RedisSeatLockAdapter implements SeatLockPort {

    private final StringRedisTemplate redisTemplate;

    private static final String UNLOCK_LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "   return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    private static final String LOCK_KEY_PREFIX = "lock:seat:";

    public RedisSeatLockAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "stringRedisTemplate cannot be null");
    }


    @Override
    public boolean acquireLock(SeatId seatId, UserId userId, Duration duration) {
        String key = buildLockKey(seatId);
        String lockOwnerValue = userId.value().toString();

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, lockOwnerValue, duration);

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void releaseLock(SeatId seatId, UserId userId) {
        String key = buildLockKey(seatId);
        String lockOwnerValue = userId.value().toString();

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Long.class);

        redisTemplate.execute(redisScript, Collections.singletonList(key), lockOwnerValue);
    }

    private String buildLockKey(SeatId seatId) {
        return LOCK_KEY_PREFIX + seatId.value();
    }
}
