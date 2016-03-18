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

/**
 * An action within the {@link Events} object. All references are hard ones, because this object should not live long.
 * 
 * @author Manfred Hantschel
 */
class Action
{

    /**
     * The types of actions
     * 
     * @author Manfred HANTSCHEL
     */
    enum Type
    {
        /**
         * Binds a consumer to a producer
         */
        BIND,

        /**
         * Unbinds a consumer from a producer
         */
        UNBIND,

        /**
         * Fires an event
         */
        FIRE
    }

    private final Type type;
    private final Object producer;
    private final Object parameter;
    private final String[] tags;

    /**
     * Creates a new action.
     * 
     * @param type the type of the action, mandatory
     * @param producer the producer, mandatory
     * @param parameter the parameter
     * @param tags the tags, optional
     * @throws IllegalArgumentException if the action or the producer is null
     */
    public Action(Type type, Object producer, Object parameter, String... tags) throws IllegalArgumentException
    {
        super();

        if (type == null)
        {
            throw new IllegalArgumentException("Type is null");
        }

        if (producer == null)
        {
            throw new IllegalArgumentException("Producer is null");
        }

        this.type = type;
        this.producer = producer;
        this.parameter = parameter;
        this.tags = tags;
    }

    /**
     * Returns the type of the action.
     * 
     * @return the type, never null
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns the producer
     * 
     * @return the producer, never null
     */
    public Object getProducer()
    {
        return producer;
    }

    /**
     * Returns the parameter
     * 
     * @return the parameter
     */
    public Object getParameter()
    {
        return parameter;
    }

    /**
     * Returns the tags
     * 
     * @return the tags
     */
    public String[] getTags()
    {
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return type.hashCode() ^ producer.hashCode() ^ ((parameter != null) ? parameter.hashCode() : -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof Action))
        {
            return false;
        }

        Action link = (Action) obj;

        return (type == link.type) && (producer == link.producer) && (parameter == link.parameter);
    }

}
