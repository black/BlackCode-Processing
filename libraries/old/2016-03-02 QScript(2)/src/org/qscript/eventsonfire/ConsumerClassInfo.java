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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Contains information about a consumer class. Scans the class for methods that may act as event handlers. Holds these
 * methods and invokes them if necessary and appropriate.
 * 
 * @author Manfred HANTSCHEL
 */
class ConsumerClassInfo implements Iterable<EventHandlerInfo>
{

    /**
     * Holds all previously generated {@link ConsumerClassInfo} objects
     */
    private static final Map<Class<?>, ConsumerClassInfo> CACHE = new HashMap<Class<?>, ConsumerClassInfo>();

    /**
     * Returns the class info for the specified consumer class. Uses a cache to speed up processing.
     * 
     * @param type the class, mandatory
     * @return the class info for the consumer, never null
     * @throws IllegalArgumentException if the type is null or the class does not contain any method annotated with the
     *             {@link EventHandler} annotation
     */
    public static ConsumerClassInfo getInstance(Class<?> type) throws IllegalArgumentException
    {
        ConsumerClassInfo result = CACHE.get(type);

        if (result != null)
        {
            return result;
        }

        result = new ConsumerClassInfo(type);

        CACHE.put(type, result);

        return result;
    }

    /**
     * All the {@link EventHandlerInfo}s for event handlers in the class
     */
    private final Collection<EventHandlerInfo> infos;

    /**
     * Creates the info for the specified consumer class.
     * 
     * @param type the class, mandatory
     * @throws IllegalArgumentException if the type is null or the class does not contain any method annotated with the
     *             {@link EventHandler} annotation
     */
    private ConsumerClassInfo(Class<?> type) throws IllegalArgumentException
    {
        super();

        if (type == null)
        {
            throw new IllegalArgumentException("Type is null");
        }

        infos = Collections.unmodifiableCollection(Events.scanConsumer(type));

        if (infos.size() == 0)
        {
            throw new IllegalArgumentException("No event handlers found in " + type);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<EventHandlerInfo> iterator()
    {
        return infos.iterator();
    }

    /**
     * Invokes all event handler methods of the class if the method is applicable for the type of producer, consumer and
     * event. If an error occurs when invoking the method, the invocationFailed method of the {@link ErrorHandler} is
     * called.
     * 
     * @param producer the producer, mandatory
     * @param consumer the consumer, mandatory
     * @param event the event, mandatory
     * @param tags the tags
     */
    public void invoke(Object producer, Object consumer, Object event, String[] tags)
    {
        for (EventHandlerInfo info : infos)
        {
            info.invoke(producer, consumer, event, tags);
        }
    }

}
