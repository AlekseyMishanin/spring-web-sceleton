package com.mishanin.server.handlers;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.StatisticsHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomStatisticsHandler extends StatisticsHandler {

    private static final String METRICS = "/metrics";

    @Override
    public void handle(String path, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (HttpMethod.GET.is(request.getMethod()) && METRICS.equals(path)) {

            baseRequest.setHandled(true);

            String content = super.toStatsHTML();

            response.setStatus(HttpStatus.OK_200);
            response.setContentType(MimeTypes.Type.TEXT_HTML.name());
            response.setContentLength(content.length());
            PrintWriter writer = response.getWriter();
            writer.write(content);
        } else {
            super.handle(path, baseRequest, request, response);
        }
    }
}
