package org.webbitserver.handler.logging;

import org.webbitserver.EventSourceConnection;
import org.webbitserver.netty.contrib.EventSourceMessage;
import org.webbitserver.wrapper.EventSourceConnectionWrapper;

class LoggingEventSourceConnection extends EventSourceConnectionWrapper {

    private final LogSink logSink;

    LoggingEventSourceConnection(LogSink logSink, EventSourceConnection connection) {
        super(connection);
        this.logSink = logSink;
    }

    @Override
    public EventSourceConnectionWrapper send(String message) {
        logSink.eventSourceOutboundData(this, message);
        return super.send(message);
    }

    @Override
    public EventSourceConnectionWrapper send(EventSourceMessage message) {
        logSink.eventSourceOutboundData(this, message.build() + "\n");
        return super.send(message);
    }
}
