package com.mishanin.server;

import com.mishanin.server.config.EmbeddedJettyConfiguration;
import com.mishanin.server.config.SpringAnnotationConfiguration;
import lombok.Builder;
import lombok.SneakyThrows;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Embedded jetty server
 */
@Builder
public class EmbeddedJettyServer {
    @Builder.Default
    private final Server server = new Server();
    private final EmbeddedJettyConfiguration configuration;

    public void run() throws Exception {
        server.setConnectors(new Connector[]{getServerConnector()});
        server.setHandler(configureAndGetWebAppContext());
        server.start();
        server.join();
    }

    @SneakyThrows
    private WebAppContext configureAndGetWebAppContext() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        // If you have overridden variable JETTY_HOME, there may be problems when loading classes in webdefault.xml
        webAppContext.setDefaultsDescriptor(null);
        webAppContext.setBaseResource(getBaseResource());
        webAppContext.setExtraClasspath("target/classes");
        webAppContext.setConfigurations(new Configuration[]{
                new SpringAnnotationConfiguration(configuration.getInitializers())
        });

        return webAppContext;
    }

    private ServerConnector getServerConnector() {
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(configuration.getHost());
        connector.setPort(configuration.getPort());
        return connector;
    }

    /**
     * @return
     * @throws URISyntaxException    if this URL is not formatted strictly according to
     *                               RFC2396 and cannot be converted to a URI.
     * @throws MalformedURLException If a protocol handler for the URL could not be found,
     *                               or if some other error occurred while constructing the URL
     */
    @SneakyThrows({URISyntaxException.class, MalformedURLException.class})
    private Resource getBaseResource() {
        URL urlAppProperties = getClass().getResource("/application.properties");
        if (urlAppProperties == null) {
            throw new NullPointerException("urlAppProperties is null, because application.properties was not found");
        }
        URI resourceUri = URI.create(urlAppProperties.toURI().toASCIIString().replaceFirst("/application.properties$", "/"));
        return Resource.newResource(resourceUri);
    }
}
