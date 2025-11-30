// FILE: src/main/java/com/natzsxn/mysmp/service/UserService.java
package com.natzsxn.mysmp.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<Void> setLastWorld(UUID playerId, String worldName);
    CompletableFuture<String> getLastWorld(UUID playerId);
}
