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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Abstract implementation of an {@link EventHandlerStrategy}, that uses simple annotations like the
 * {@link EventHandler}, {@link PooledEventHandler} or {@link SwingEventHandler} annotations.
 * 
 * @author Manfred HANTSCHEL
 * @param <ANNOTATION_TYPE> the type of the annotation
 */
public abstract class AbstractAnnotatedEventHandlerStrategy<ANNOTATION_TYPE extends Annotation> extends
    AbstractEventHandlerStrategy
{

    public AbstractAnnotatedEventHandlerStrategy()
    {
        super();
    }

    /**
     * Returns the type of the annotation
     * 
     * @return the type of the annotation
     */
    protected abstract Class<ANNOTATION_TYPE> getAnnotationType();

    /**
     * Returns the allowed producer types as specified in the annotation. If the annotation does not specify any special
     * producer types, the method returns an empty array.
     * 
     * @param annotation the annotation
     * @param method the method
     * @return an array of producer types, never null
     */
    protected abstract Class<?>[] getAllowedProducerTypes(ANNOTATION_TYPE annotation, Method method);

    /**
     * Returns the allowed event types as specified in the annotation. If the annotation does not specify any special
     * event types, the method returns an empty array.
     * 
     * @param annotation the annotation
     * @param method the method
     * @return an array of event types, never null
     */
    protected abstract Class<?>[] getAllowedEventTypes(ANNOTATION_TYPE annotation, Method method);

    /**
     * Returns the anyTag of the annotation.
     * 
     * @param annotation the annotation
     * @param method the method
     * @return an array of strings, never null
     */
    protected abstract String[] getAnyTags(ANNOTATION_TYPE annotation, Method method);

    /**
     * Returns the eachTag of the annotation.
     * 
     * @param annotation the annotation
     * @param method the method
     * @return an array of strings, never null
     */
    protected abstract String[] getEachTags(ANNOTATION_TYPE annotation, Method method);

    /**
     * {@inheritDoc}
     */
    @Override
    protected EventHandlerInfo createEventHandlerInfo(Method method)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("Method is null");
        }

        ANNOTATION_TYPE annotation = method.getAnnotation(getAnnotationType());

        if (annotation == null)
        {
            return null;
        }

        return createEventHandlerInfo(annotation, method, getAllowedProducerTypes(annotation, method),
            getAllowedEventTypes(annotation, method), getAnyTags(annotation, method), getEachTags(annotation, method));
    }

    /**
     * Called to create the {@link EventHandlerInfo}.
     * 
     * @param annotation the annotation
     * @param method the method
     * @param producerTypesByAnnotation the producer types
     * @param eventTypesByAnnotation the event types
     * @param anyTagsByAnnotation the any tags
     * @param eachTagsByAnnotation the each tags
     * @return the event handler information object
     */
    protected abstract EventHandlerInfo createEventHandlerInfo(ANNOTATION_TYPE annotation, Method method,
        Class<?>[] producerTypesByAnnotation, Class<?>[] eventTypesByAnnotation, String[] anyTagsByAnnotation,
        String[] eachTagsByAnnotation);

}
