package com.mishanin.server.listeners;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;

/**
 * HttpChannel notifies {@link HttpChannel.Listener} of the progress of the HTTP request/response handling.
 * Currently, the following events are available:
 * requestBegin
 * beforeDispatch
 * dispatchFailure
 * afterDispatch
 * requestContent
 * requestContentEnd
 * requestTrailers
 * requestEnd
 * responseBegin
 * responseCommit
 * responseContent
 * responseFailure
 * responseEnd
 * complete
 */
public class Http11Listener implements HttpChannel.Listener {
    @Override
    public void onRequestBegin(Request request) {
        // do something
    }
}
