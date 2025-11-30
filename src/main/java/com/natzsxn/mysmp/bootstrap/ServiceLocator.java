// FILE: src/main/java/com/natzsxn/mysmp/bootstrap/ServiceLocator.java
package com.natzsxn.mysmp.bootstrap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceLocator {
    private final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();

    public <T> void register(Class<T> type, T instance) {
        registry.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) registry.get(type);
    }
}
