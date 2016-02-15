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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.qscript.eventsonfire.AbstractEventHandlerInfo.MethodType;

/**
 * Runnable used for event handler invocations by other threads
 * 
 * @author Manfred HANTSCHEL
 */
public class EventHandlerInvoker implements Runnable
{

    private final MethodType methodType;
    private final Method method;
    private final Object producer;
    private final Object consumer;
    private final Object event;
    private final String[] tags;

    public EventHandlerInvoker(MethodType methodType, Method method, Object producer, Object consumer, Object event,
        String... tags)
    {
        super();

        this.methodType = methodType;
        this.method = method;
        this.producer = producer;
        this.consumer = consumer;
        this.event = event;
        this.tags = tags;
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            method.invoke(consumer, methodType.toParameters(producer, event, tags));
        }
        catch (final IllegalArgumentException e)
        {
            Events.getErrorHandler().invocationFailed(method, "Invalid argument", e, producer, consumer, event, tags);
        }
        catch (final IllegalAccessException e)
        {
            Events.getErrorHandler().invocationFailed(method, "Illegal access", e, producer, consumer, event, tags);
        }
        catch (final InvocationTargetException e)
        {
            Events.getErrorHandler().invocationFailed(method, "Invocation failed", e, producer, consumer, event, tags);
        }
        catch (final Exception e)
        {
            Events.getErrorHandler().invocationFailed(method, "Unhandled exception", e, producer, consumer, event, tags);
        }
    }
}
