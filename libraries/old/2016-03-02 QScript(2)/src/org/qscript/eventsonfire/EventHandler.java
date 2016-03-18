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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Tags a method as event handler within a consumer, which may be called by the {@link Events} class. The method needs
 * two arguments, the producer and the event. The producer is optional. The method will only be called if the type of
 * the producer and the event fits the parameters and optionally the specified types.
 * </p>
 * 
 * @author Manfred HANTSCHEL
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler
{

    /**
     * Optional. One or more classes of producers handled by the method. Checked against the optional producer argument.
     * If empty and a producer argument is specified, all classes are allowed that fit the producer argument. If empty
     * and no producer argument is specified, all producers are allowed.
     * 
     * @return the allowed producers
     */
    Class<?>[] producer() default {};

    /**
     * Optional. One or more classes of events handled by the method. Checked against the event argument. If empty, all
     * classes are allowed that fit the events argument.
     * 
     * @return the allowed events
     */
    Class<?>[] event() default {};

    /**
     * Optional. One or more tags - the event has to be fired with at least one of these tags to trigger the event
     * handler.
     * 
     * @return an array of strings
     */
    String[] anyTag() default {};

    /**
     * Optional. One or more tags - the event has to be fired with all of these tags to trigger the event handler.
     * 
     * @return an array of strings
     */
    String[] eachTag() default {};

    /**
     * Optional. If set to true, the invocation of the method will be delegated to a thread pool. The execution of the
     * method will not block the event thread. The default value is false, because usually event handler are quite fast
     * and should be execute one after another.
     * 
     * @return the type of the invocation of the method
     * @deprecated use the {@link PooledEventHandler} annotation instead
     */
    @Deprecated
    boolean pooled() default false;

}
