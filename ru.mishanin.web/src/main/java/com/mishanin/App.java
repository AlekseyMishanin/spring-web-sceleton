package com.mishanin;

import com.mishanin.config.WebInitializer;
import com.mishanin.config.WebSecurityInitializer;
import com.mishanin.server.EmbeddedJettyServer;
import com.mishanin.server.config.EmbeddedJettyConfiguration;

import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        EmbeddedJettyServer.builder()
                .configuration(EmbeddedJettyConfiguration.builder()
                        .initializers(List.of(WebInitializer.class, WebSecurityInitializer.class))
                        .build())
                .build()
                .run();
    }
}
