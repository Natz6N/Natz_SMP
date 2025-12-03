package com.natzdev.natzsmp.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceRegistry {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    private ServiceRegistry() {}

    public static <T> void register(Class<T> type, T impl) {
        SERVICES.put(type, impl);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        return (T) SERVICES.get(type);
    }

    public static void clear() {
        SERVICES.clear();
    }
}
