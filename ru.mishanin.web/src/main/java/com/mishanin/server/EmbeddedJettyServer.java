package com.mishanin.server;

import com.mishanin.server.config.EmbeddedJettyConfiguration;
import com.mishanin.server.config.SpringAnnotationConfiguration;
import com.mishanin.server.handlers.LogHandler;
import lombok.SneakyThrows;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Embedded jetty server
 */
public class EmbeddedJettyServer {

    private final Server server;
    private final EmbeddedJettyConfiguration configuration;

    public EmbeddedJettyServer(EmbeddedJettyConfiguration configuration) {
        this.server = new Server(getThreadPool());
        this.configuration = configuration;
    }

    public void run() throws Exception {
        server.addConnector(getServerConnector());
        server.setHandler(configureAndGetWebAppContext());
        server.setDumpAfterStart(true);
        server.setDumpBeforeStop(true);
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

    /**
     * Create and configure a ThreadPool.
     *
     * @return QueuedThreadPool
     */
    private QueuedThreadPool getThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName("jetty-server");
        return threadPool;
    }

    private ServerConnector getServerConnector() {

        int cores = ProcessorUtils.availableProcessors();

        // There is a little moment where the acceptor thread is not accepting new connections
        // because it is busy wrapping the just accepted connection to pass it to the SelectorManager.
        // Configuring more than one acceptor thread may be beneficial: when one acceptor thread accepts
        // one connection, another acceptor thread can take over accepting connections.
        int acceptors = Math.max(1, Math.min(4, cores / 8));

        // A single selector can easily manage up to 1000-5000 sockets.
        int selectors = Math.max(1, cores / 2);

        HttpConfiguration httpConfig = new HttpConfiguration();
        // Add the SecureRequestCustomizer because we are using TLS. For localhost we need to remove sniHost check.
        httpConfig.addCustomizer(new SecureRequestCustomizer(false));
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        // TODO read external variables in a separate method
        sslContextFactory.setKeyStorePath(System.getProperty("keystore.path"));
        sslContextFactory.setKeyStorePassword(System.getProperty("keystore.password"));
        // JKS for test
        sslContextFactory.setKeyStoreType("JKS");
        SslConnectionFactory tls = new SslConnectionFactory(sslContextFactory, http11.getProtocol());

        // It is possible to configure more than one ServerConnector, each listening on a different port.
        ServerConnector connector = new ServerConnector(server, acceptors, selectors, tls, http11);
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
