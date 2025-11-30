// FILE: src/main/java/com/natzsxn/mysmp/task/TeleportCooldownTask.java
package com.natzsxn.mysmp.task;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportCooldownTask {
    private final Map<UUID, Long> map = new ConcurrentHashMap<>();

    public boolean tryAcquire(UUID id, int seconds) {
        long now = System.currentTimeMillis();
        long until = map.getOrDefault(id, 0L);
        if (now < until) return false;
        map.put(id, now + seconds * 1000L);
        return true;
    }
}
