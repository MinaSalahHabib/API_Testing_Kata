package com.booking.config;

/** Centralized configuration; secrets can be supplied via system properties or environment variables. */
public final class ApiConfig {
    private static final String DEFAULT_BASE_URL = "https://automationintesting.online/api";
    private ApiConfig() { }

    public static String baseUrl() {
        return firstNonBlank(System.getProperty("api.baseUrl"), System.getenv("API_BASE_URL"), DEFAULT_BASE_URL).replaceAll("/+$", "");
    }
    public static String username() {
        return firstNonBlank(System.getProperty("api.username"), System.getenv("API_USERNAME"), "admin");
    }
    public static String password() {
        return firstNonBlank(System.getProperty("api.password"), System.getenv("API_PASSWORD"), "password");
    }
    private static String firstNonBlank(String... values) {
        for (String value : values) if (value != null && !value.isBlank()) return value.trim();
        throw new IllegalStateException("No configuration value supplied");
    }
}
