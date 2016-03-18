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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * A weak reference with a hashCode and an equals method that checks for identity of the referent. Warning: the equals
 * methods only works, if the referent has not been garbage collected. After the removal of the referent the equals
 * method only checks for identity of the reference, not the referent.
 * 
 * @author Manfred Hantschel
 * @param <TYPE> the type of the reference
 */
class WeakIdentityReference<TYPE> extends WeakReference<TYPE>
{

    private final int hashCode;

    /**
     * Creates the weak identity reference that refers to the specified referent. The reference is not registered to any
     * queue.
     * 
     * @param referent the object referenced by the weak reference, mandatory
     * @throws IllegalArgumentException if the referent is null
     */
    public WeakIdentityReference(final TYPE referent) throws IllegalArgumentException
    {
        this(referent, null);
    }

    /**
     * Creates the weak identity reference with the specified referent. The reference is registered with the specified
     * queue.
     * 
     * @param referent the object referenced by the weak reference, mandatory
     * @param queue the queue with which the reference is to be registered, or null if registration is not required
     * @throws IllegalArgumentException if the referent is null
     */
    public WeakIdentityReference(final TYPE referent, final ReferenceQueue<? super TYPE> queue)
        throws IllegalArgumentException
    {
        super(referent, queue);

        if (referent == null)
        {
            throw new IllegalArgumentException("Referent is null");
        }

        hashCode = referent.hashCode();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof WeakIdentityReference))
        {
            return false;
        }

        final TYPE referent = get();

        if (referent == null)
        {
            return false;
        }

        return referent == ((WeakIdentityReference<?>) obj).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "WeakIdentityReference of " + get();
    }

}
