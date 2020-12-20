package com.mishanin;

import com.mishanin.config.WebInitializer;
import com.mishanin.config.WebSecurityInitializer;
import com.mishanin.server.config.SpringAnnotationConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class App {

    public static void main(String[] args) throws Exception {

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setBaseResource(App.resolveWebBaseResource());
        webAppContext.setContextPath("/");
        webAppContext.setConfigurations(new Configuration[] {
                new WebXmlConfiguration(),
                new SpringAnnotationConfiguration(Arrays.asList(WebInitializer.class, WebSecurityInitializer.class))
        });

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(9090);

        server.setConnectors(new Connector[]{connector});
        server.setHandler(webAppContext);
        server.start();
        server.join();
    }

    private static Resource resolveWebBaseResource() throws MalformedURLException, URISyntaxException {
        try {
            try {
                URL resource = App.class.getResource("/application.properties");
                if (resource == null) {
                    throw new IllegalStateException("Unable to determine location for application.properties");
                } else {
                    URI baseResourceURI = URI.create(resource.toURI().toASCIIString().replaceFirst("/application.properties$", "/"));
                    return Resource.newResource(baseResourceURI);
                }
            } catch (URISyntaxException var3) {
                throw var3;
            }
        } catch (IOException var4) {
            throw var4;
        }
    }
}
