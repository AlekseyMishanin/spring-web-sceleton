package com.mishanin.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Plugin(
        name = "IgnoreExceptionFilter",
        category = Node.CATEGORY,
        elementType = Filter.ELEMENT_TYPE
)
public class IgnoreExceptionFilter extends AbstractFilter {

    private static final String EOF_EXCEPTION = "EofException";
    private static final String REQUEST_REJECTED_EXCEPTION = "RequestRejectedException";

    // key - name of the exception, value - list of messages
    private static final Map<String, List<String>> ignorableExceptions = Map.of(EOF_EXCEPTION, Collections.emptyList(), REQUEST_REJECTED_EXCEPTION, Collections.singletonList("The request was rejected because the URL contained a potentially malicious"));

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return this.filter(t);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return this.filter(t);
    }

    @Override
    public Result filter(LogEvent event) {
        return this.filter(event.getThrown());
    }

    public Result filter(Throwable throwable) {
        if (isIgnoredClientException(throwable)) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    /**
     * Check whether the exception should be ignored
     *
     * @param throwable exception to check
     * @return true if the exception is present in the list
     */
    private boolean isIgnoredClientException(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            String className = cause.getClass().getSimpleName();
            Throwable finalCause = cause;
            if (ignorableExceptions.entrySet().stream()
                    .filter((exception) -> className.equals(exception.getKey()))
                    // If the message list is empty, then we ignore any messages,
                    // otherwise we compare the received message with the list of ignore messages
                    .anyMatch(filterEntry -> filterEntry.getValue().isEmpty() ||
                            filterEntry.getValue().stream()
                                    .anyMatch(message -> finalCause.getMessage().startsWith(message)))) {
                return true;
            }

            cause = cause.getCause();
        }
        return false;
    }

    @PluginFactory
    public static IgnoreExceptionFilter createFilter() {
        return new IgnoreExceptionFilter();
    }
}
