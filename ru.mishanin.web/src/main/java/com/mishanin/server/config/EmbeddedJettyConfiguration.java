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
    private final List<Class<? extends WebApplicationInitializer>> initializers;
}
