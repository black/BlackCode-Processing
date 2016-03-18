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

/**
 * Handler for errors within the Events mechanism.
 * 
 * @author Manfred HANTSCHEL
 */
public interface ErrorHandler
{

    /**
     * Called if the invocation of an event handler method fails.
     * @param method the method which caused the error
     * @param message some informative message
     * @param cause the exception if available, may be null
     * @param producer the producer of the event
     * @param consumer the consumer of the event
     * @param event the event itself
     * @param tags the tags, if any
     */
    void invocationFailed(Method method, String message, Throwable cause, Object producer, Object consumer, Object event, String... tags);

    /**
     * Called if a unhandled exception occurs in the event handler thread.
     * 
     * @param message the message
     * @param cause the cause
     */
    void unhandledException(String message, Throwable cause);

    /**
     * Called if the event thread got interrupted.
     * 
     * @param e the interrupted exception
     */
    void interrupted(InterruptedException e);

}
