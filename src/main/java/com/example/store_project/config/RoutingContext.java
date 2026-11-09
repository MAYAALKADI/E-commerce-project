package com.example.store_project.config;

public final class RoutingContext {

    private static final ThreadLocal<Boolean> FORCE_PRIMARY = new ThreadLocal<>();

    private RoutingContext() {}

    public static void forcePrimary() { FORCE_PRIMARY.set(Boolean.TRUE); }

    public static void clear() { FORCE_PRIMARY.remove(); }

    public static boolean isForcePrimary() { return Boolean.TRUE.equals(FORCE_PRIMARY.get()); }
}