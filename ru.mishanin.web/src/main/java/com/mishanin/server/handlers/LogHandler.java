package com.mishanin.server.handlers;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A handler that logs all requests.
 * NOTE: For debugging.
 */
@Log4j2
public class LogHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request jettyRequest,
                       HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // TODO you can check any request/response parameter
        log.info("Received a request for {}", target);
    }
}
