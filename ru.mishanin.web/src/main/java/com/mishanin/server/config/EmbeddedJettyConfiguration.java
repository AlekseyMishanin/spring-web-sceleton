package com.mishanin.server.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.WebApplicationInitializer;

import java.util.List;

/**
 * The class describes the settings used when starting the jetty server
 */
@Getter
@Builder
public class EmbeddedJettyConfiguration {
    @Builder.Default
    private final String host = "127.0.0.1";
    @Builder.Default
    private final int port = 8080;
    @Builder.Default
    private final String contextPath = "/";
    @Builder.Default
    private final String extraClasspath = "target/classes";
    @Builder.Default
    private final String threadPoolName = "jetty-server";
    @Builder.Default
    private final boolean enableDebug = false;
    @Builder.Default
    private final boolean enableTLS = false;
    @Builder.Default
    private final String keyStorePath = System.getProperty("keystore.path");
    @Builder.Default
    private final String keyStorePassword = System.getProperty("keystore.password");
    @Builder.Default
    private final String keyStoreType = "JKS";
    private final List<Class<? extends WebApplicationInitializer>> initializers;

    public String getKeyStorePath() {
        return getValueOrThrow(keyStorePath);
    }

    public String getKeyStorePassword() {
        return getValueOrThrow(keyStorePassword);
    }

    /**
     * Check if passed argument exists. If it exists then we return it otherwise we throw an exception.
     *
     * @param value Value for check
     * @return Value or exception
     * @throws IllegalArgumentException if value does not exist
     */
    private String getValueOrThrow(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s is not present.", value));
        }
        return value;
    }
}
