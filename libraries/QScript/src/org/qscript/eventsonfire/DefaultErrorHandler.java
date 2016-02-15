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

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Default implementation of the {@link ErrorHandler}. Writes messages to writer.
 * 
 * @author Manfred HANTSCHEL
 */
public class DefaultErrorHandler implements ErrorHandler
{

    private final PrintStream stream;

    /**
     * Default constructor for the error handler, writes to System.err
     */
    public DefaultErrorHandler()
    {
        this(System.err);
    }

    /**
     * Creates an error handler using the specified stream
     * 
     * @param stream the stream
     */
    public DefaultErrorHandler(PrintStream stream)
    {
        super();

        this.stream = stream;
    }

    /**
     * {@inheritDoc}
     */
    public void invocationFailed(final Method method, final String message, final Throwable cause, final Object producer,
        final Object consumer, final Object event, String... tags)
    {
        stream.println("Invocation of event handler failed: " + message);
        stream.println("\tMethod:   " + method);
        stream.println("\tProducer: " + producer);
        stream.println("\tConsumer: " + consumer);
        stream.println("\tEvent:    " + event);
        stream.println("\tTags:     " + Arrays.toString(tags));

        if (cause != null)
        {
            stream.print("\tCause:    ");
            cause.printStackTrace(stream);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unhandledException(final String message, final Throwable cause)
    {
        stream.println("UNHANDLED EXCEPTION: " + message);
        cause.printStackTrace(stream);
    }

    /**
     * {@inheritDoc}
     */
    public void interrupted(InterruptedException e)
    {
        stream.println("Events thread got interrupted.");
        e.printStackTrace(stream);
    }

}
