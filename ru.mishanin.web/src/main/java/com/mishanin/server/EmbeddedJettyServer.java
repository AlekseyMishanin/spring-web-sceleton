package com.mishanin.server;

import com.mishanin.server.config.EmbeddedJettyConfiguration;
import com.mishanin.server.config.SpringAnnotationConfiguration;
import com.mishanin.server.handlers.CustomStatisticsHandler;
import com.mishanin.server.listeners.Http11Listener;
import com.mishanin.server.listeners.LifeCycleListener;
import lombok.SneakyThrows;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DebugHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
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
        this.configuration = configuration;
        this.server = new Server(getThreadPool());
    }

    public void run() throws Exception {
        LifeCycleListener lifeCycleListener = new LifeCycleListener();
        // GzipHandler provides supports for automatic decompression of
        // compressed request content and automatic compression of response content.
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.addEventListener(lifeCycleListener);
        gzipHandler.setMinGzipSize(512);
        gzipHandler.setHandler(configureAndGetWebAppContext());

        CustomStatisticsHandler statisticsHandler = new CustomStatisticsHandler();
        statisticsHandler.addEventListener(lifeCycleListener);
        statisticsHandler.setHandler(gzipHandler);

        // invokes all Handlers one after the other
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(statisticsHandler);
        if (configuration.isEnableDebug()) {
            DebugHandler debugHandler = new DebugHandler();
            debugHandler.addEventListener(lifeCycleListener);
            handlers.addHandler(debugHandler);
        }
        server.addConnector(getServerConnector());
        server.setHandler(handlers);
        server.start();
        server.join();
    }

    @SneakyThrows
    private WebAppContext configureAndGetWebAppContext() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(configuration.getContextPath());
        // If you have overridden variable JETTY_HOME, there may be problems when loading classes in webdefault.xml
        webAppContext.setDefaultsDescriptor(null);

        //  webAppContext.setBaseResource(getBaseResource());
        webAppContext.setExtraClasspath(configuration.getExtraClasspath());
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
        threadPool.setName(configuration.getThreadPoolName());
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
        ServerConnector connector = new ServerConnector(server, acceptors, selectors, getConnectionFactories());
        connector.addBean(new Http11Listener());
        connector.setHost(configuration.getHost());
        connector.setPort(configuration.getPort());
        // Connections that are ready to be accepted but are not accepted yet are queued in a bounded queue (at the OS level)
        connector.setAcceptQueueSize(32);
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
            throw new IllegalArgumentException("application.properties was not found");
        }
        URI resourceUri = URI.create(urlAppProperties.toURI().toASCIIString().replaceFirst("/application.properties$", "/"));
        return Resource.newResource(resourceUri);
    }

    private ConnectionFactory[] getConnectionFactories() {
        if (configuration.isEnableTLS()) {
            HttpConfiguration httpConfig = new HttpConfiguration();
            // Add the SecureRequestCustomizer because we are using TLS. For localhost we need to remove sniHost check.
            httpConfig.addCustomizer(new SecureRequestCustomizer(false));
            HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(configuration.getKeyStorePath());
            sslContextFactory.setKeyStorePassword(configuration.getKeyStorePassword());
            sslContextFactory.setKeyStoreType(configuration.getKeyStoreType());
            SslConnectionFactory tls = new SslConnectionFactory(sslContextFactory, http11.getProtocol());
            return new ConnectionFactory[]{tls, http11};
        }
        return new ConnectionFactory[]{new HttpConnectionFactory()};
    }
}
