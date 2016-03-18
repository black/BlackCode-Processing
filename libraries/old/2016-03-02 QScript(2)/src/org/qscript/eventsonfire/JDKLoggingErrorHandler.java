/*
 * Copyright (c) 2011, 2012 events-on-fire Team
 * 
 * This file is part of Events-On-Fire (http://code.google.com/p/events-on-fire), licensed under the terms of the MIT
 * License (MIT).
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.qscript.eventsonfire;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the {@link ErrorHandler} for the Java logging framework.
 * 
 * @author Manfred HANTSCHEL
 */
public class JDKLoggingErrorHandler implements ErrorHandler
{

    private static final Logger LOG = Logger.getLogger(JDKLoggingErrorHandler.class.getName());

    private final Logger log;

    /**
     * Default constructor for the error handler
     */
    public JDKLoggingErrorHandler()
    {
        this(LOG);
    }

    /**
     * Constructor for the error handler using the specified logger
     * 
     * @param log the logger
     */
    public JDKLoggingErrorHandler(Logger log)
    {
        super();

        this.log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void invocationFailed(final Method method, final String message, final Throwable cause, final Object producer,
        final Object consumer, final Object event, String... tags)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("Invocation of event handler failed: ").append(message);
        builder.append("\n\tMethod:   ").append(method);
        builder.append("\n\tProducer: ").append(producer);
        builder.append("\n\tConsumer: ").append(consumer);
        builder.append("\n\tEvent:    ").append(event);
        builder.append("\n\tTags:     ").append(Arrays.toString(tags));

        log.log(Level.SEVERE, builder.toString(), cause);
    }

    /**
     * {@inheritDoc}
     */
    public void unhandledException(final String message, final Throwable cause)
    {
        log.log(Level.SEVERE, "UNHANDLED EXCEPTION: " + message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public void interrupted(InterruptedException e)
    {
        log.log(Level.SEVERE, "Events thread got interrupted", e);
    }

}
