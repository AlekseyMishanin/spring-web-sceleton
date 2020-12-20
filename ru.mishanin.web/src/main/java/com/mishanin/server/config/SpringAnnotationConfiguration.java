package com.mishanin.server.config;

import lombok.AllArgsConstructor;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class SpringAnnotationConfiguration extends AnnotationConfiguration {

    private final List<Class<? extends WebApplicationInitializer>> webAppInitializers;

    @Override
    public void preConfigure(WebAppContext context) {

        ConcurrentHashMap.KeySetView<String, Boolean> initializerClasses = ConcurrentHashMap.newKeySet();

        for (Class<?> webAppInitializer : webAppInitializers) {
            initializerClasses.add(webAppInitializer.getName());
        }

        ClassInheritanceMap map = new ClassInheritanceMap();
        map.put(WebApplicationInitializer.class.getName(), initializerClasses);

        context.setAttribute(CLASS_INHERITANCE_MAP, map);
        _classInheritanceHandler = new ClassInheritanceHandler(map);
    }
}
