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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of an {@link EventHandlerInfo} based on a method with possible and allowed producer and event
 * types.
 * 
 * @author Manfred HANTSCHEL
 */
public abstract class AbstractEventHandlerInfo implements EventHandlerInfo
{

    public enum MethodType
    {

        NO_PARAMETERS,

        EVENT,

        TAGS,

        EVENT_TAGS,

        PROVIDER_EVENT,

        PROVIDER_EVENT_TAGS;

        public Object[] toParameters(Object producer, Object event, String... tags)
        {
            switch (this)
            {
                case NO_PARAMETERS:
                    return null;

                case EVENT:
                    return new Object[]{event};

                case TAGS:
                    return new Object[]{tags};

                case EVENT_TAGS:
                    return new Object[]{event, tags};

                case PROVIDER_EVENT:
                    return new Object[]{producer, event};

                case PROVIDER_EVENT_TAGS:
                    return new Object[]{producer, event, tags};

                default:
                    throw new IllegalArgumentException("Unsupported method type: " + this);
            }
        }
    }

    protected final MethodType methodType;
    protected final Method method;
    protected final Class<?>[] producerTypes;
    protected final Class<?>[] eventTypes;
    protected final Set<String> anyTags;
    protected final Set<String> eachTags;

    public AbstractEventHandlerInfo(Method method, Class<?>[] producerTypesByAnnotation,
        Class<?>[] eventTypesByAnnotation, String[] anyTagsByAnnotation, String[] eachTagsByAnnotation)
    {
        super();

        validateReturnType(method);

        this.method = method;

        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0)
        {
            // void eventHandler() {...}

            validateEventTypeByAnnotationNotEmpty(method, eventTypesByAnnotation);

            methodType = MethodType.NO_PARAMETERS;
            producerTypes = toArray(null, producerTypesByAnnotation);
            eventTypes = toArray(null, eventTypesByAnnotation);
        }
        else if (parameterTypes.length == 1)
        {
            if (method.isVarArgs())
            {
                // void eventHandler(tags...) {...}

                validateEventTypeByAnnotationNotEmpty(method, eventTypesByAnnotation);
                validateTagsOfString(method, parameterTypes[0]);

                methodType = MethodType.TAGS;
                producerTypes = toArray(null, producerTypesByAnnotation);
                eventTypes = toArray(null, eventTypesByAnnotation);
            }
            else
            {
                // void eventHandler(event) {...}

                validateAssignableEvent(method, parameterTypes[0], eventTypesByAnnotation);

                methodType = MethodType.EVENT;
                producerTypes = toArray(null, producerTypesByAnnotation);
                eventTypes = toArray(parameterTypes[0], eventTypesByAnnotation);
            }
        }
        else if (parameterTypes.length == 2)
        {
            if (method.isVarArgs())
            {
                // void eventHandler(event, tags...) {...}

                validateAssignableEvent(method, parameterTypes[0], eventTypesByAnnotation);
                validateTagsOfString(method, parameterTypes[1]);

                methodType = MethodType.EVENT_TAGS;
                producerTypes = toArray(null, producerTypesByAnnotation);
                eventTypes = toArray(parameterTypes[0], eventTypesByAnnotation);

            }
            else
            {
                // void eventHandler(provider, event) {...}

                validateAssignableProvider(method, parameterTypes[0], producerTypesByAnnotation);
                validateAssignableEvent(method, parameterTypes[1], eventTypesByAnnotation);

                methodType = MethodType.PROVIDER_EVENT;
                producerTypes = toArray(parameterTypes[0], producerTypesByAnnotation);
                eventTypes = toArray(parameterTypes[1], eventTypesByAnnotation);
            }
        }
        else if (parameterTypes.length == 3)
        {
            // void eventHandler(provider, event, tags...) {...}

            validateAssignableProvider(method, parameterTypes[0], producerTypesByAnnotation);
            validateAssignableEvent(method, parameterTypes[1], eventTypesByAnnotation);
            validateTagsOfString(method, parameterTypes[2]);

            methodType = MethodType.PROVIDER_EVENT_TAGS;
            producerTypes = toArray(parameterTypes[0], producerTypesByAnnotation);
            eventTypes = toArray(parameterTypes[1], eventTypesByAnnotation);
        }
        else
        {
            throw new IllegalArgumentException("Invalid event handler signature: " + method);
        }

        anyTags = toSet(null, anyTagsByAnnotation);
        eachTags = toSet(null, eachTagsByAnnotation);
    }

    /**
     * Returns the method type
     * 
     * @return the method type
     */
    public MethodType getMethodType()
    {
        return methodType;
    }

    /**
     * Returns the method
     * 
     * @return the method
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     * Returns all allowed producer types
     * 
     * @return an array of producer types
     */
    public Class<?>[] getProducerTypes()
    {
        return producerTypes;
    }

    /**
     * Returns all allowed event types
     * 
     * @return an array of event types
     */
    public Class<?>[] getEventTypes()
    {
        return eventTypes;
    }

    /**
     * Returns the any tags
     * 
     * @return the any tags
     */
    public Set<String> getAnyTags()
    {
        return anyTags;
    }

    /**
     * Returns the each tags
     * 
     * @return the each tags
     */
    public Set<String> getEachTags()
    {
        return eachTags;
    }

    /**
     * {@inheritDoc}
     */
    public boolean invoke(Object producer, Object consumer, Object event, String... tags)
    {
        if (!isCallable(producer.getClass(), event.getClass(), tags))
        {
            return false;
        }

        call(producer, consumer, event, tags);

        return true;
    }

    /**
     * If the event handler is invokable, this method calls it
     * 
     * @param producer the producer
     * @param consumer the consumer
     * @param event the event
     * @param tags the tags
     */
    protected abstract void call(Object producer, Object consumer, Object event, String... tags);

    /**
     * Returns true if the event handler is callable
     * 
     * @param producerType the type of the producer
     * @param eventType the type of the event
     * @param tags some tags
     * @return true if invokable
     */
    protected boolean isCallable(final Class<?> producerType, final Class<?> eventType, String... tags)
    {
        return isProducerAssignable(producerType) && isEventAssignable(eventType) && isTagsMatching(tags);
    }

    /**
     * Returns true if the producer is permitted by the annotation and the parameter
     * 
     * @param type the type of the producer
     * @return true if permitted
     */
    protected boolean isProducerAssignable(final Class<?> type)
    {
        if (producerTypes == null)
        {
            return true;
        }

        for (final Class<?> producerType : producerTypes)
        {
            if (producerType.isAssignableFrom(type))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the event is permitted by the annotation and the parameter
     * 
     * @param type the type of the event
     * @return true if permitted
     */
    protected boolean isEventAssignable(final Class<?> type)
    {
        for (final Class<?> eventType : eventTypes)
        {
            if (eventType.isAssignableFrom(type))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean isTagsMatching(String[] tags)
    {
        return (isAnyTagsMatching(tags)) && (isEachTagsMatching(tags));
    }

    protected boolean isAnyTagsMatching(String[] tags)
    {
        if (anyTags == null)
        {
            return true;
        }

        for (String tag : tags)
        {
            if (anyTags.contains(tag))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean isEachTagsMatching(String[] tags)
    {
        if (eachTags == null)
        {
            return true;
        }

        for (String tag : tags)
        {
            if (!eachTags.contains(tag))
            {
                return false;
            }
        }

        return true;
    }

    protected static void validateAssignableProvider(Method method, Class<?> parameterType,
        Class<?>[] producerTypesByAnnotation)
    {
        if (producerTypesByAnnotation != null)
        {
            for (Class<?> providerByAnnotation : producerTypesByAnnotation)
            {
                if (!parameterType.isAssignableFrom(providerByAnnotation))
                {
                    throw new IllegalArgumentException(
                        "Invalid event handler signature. The provider parameter cannot accept all provider types of the annotation: "
                            + method);
                }
            }
        }
    }

    protected static void validateAssignableEvent(Method method, Class<?> parameterType,
        Class<?>[] eventTypesByAnnotation)
    {
        if (eventTypesByAnnotation != null)
        {
            for (Class<?> eventByAnnotation : eventTypesByAnnotation)
            {
                if (!parameterType.isAssignableFrom(eventByAnnotation))
                {
                    throw new IllegalArgumentException(
                        "Invalid event handler signature. The event parameter cannot accept all event types of the annotation: "
                            + method);
                }
            }
        }
    }

    protected static void validateTagsOfString(Method method, Class<?> parameterType)
    {
        if ((!parameterType.isArray()) || (!parameterType.getComponentType().equals(String.class)))
        {
            throw new IllegalArgumentException(
                "Invalid event handler signature. The tags parameter must be of type String...: " + method);
        }
    }

    protected static void validateEventTypeByAnnotationNotEmpty(Method method, Class<?>[] eventTypesByAnnotation)
    {
        if ((eventTypesByAnnotation == null) || (eventTypesByAnnotation.length == 0))
        {
            throw new IllegalArgumentException(
                "Invalid event handler signature. If method does not provide an event type, "
                    + "it must be specified in the annotation: " + method);
        }
    }

    protected static void validateReturnType(Method method)
    {
        if (method.getReturnType() != Void.TYPE)
        {
            throw new IllegalArgumentException("Invalid event handler signature. Return type must be void of " + method);
        }
    }

    protected static <TYPE> TYPE[] toArray(TYPE fallback, TYPE... values)
    {
        if ((values == null) || (values.length == 0))
        {
            if (fallback == null)
            {
                return null;
            }

            @SuppressWarnings("unchecked")
            TYPE[] result = (TYPE[]) Array.newInstance(fallback.getClass(), 1);

            result[0] = fallback;

            return result;
        }

        return values;
    }

    protected static <TYPE> Set<TYPE> toSet(TYPE fallback, TYPE... values)
    {
        if ((values == null) || (values.length == 0))
        {
            if (fallback == null)
            {
                return null;
            }

            Set<TYPE> result = new HashSet<TYPE>();

            result.add(fallback);

            return result;
        }

        return new HashSet<TYPE>(Arrays.asList(values));
    }
}
