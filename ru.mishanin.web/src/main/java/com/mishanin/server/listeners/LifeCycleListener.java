package com.mishanin.server.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.util.component.LifeCycle;

@Log4j2
@AllArgsConstructor
public class LifeCycleListener implements LifeCycle.Listener {

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        log.info("{} is starting", event);
    }

    @Override
    public void lifeCycleStarted(LifeCycle event) {
        log.info("{} was started", event);
    }

    @Override
    public void lifeCycleFailure(LifeCycle event, Throwable failure) {
        log.warn("{} failed to start", event, failure);
    }

    @Override
    public void lifeCycleStopped(LifeCycle event) {
        log.info("{} has been stopped", event);
    }
}
