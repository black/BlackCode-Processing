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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.qscript.eventsonfire.Action.Type;

/**
 * <p>
 * The most important class for events-on-fire.
 * </p>
 * <p>
 * For a detailed usage description see <a href="http://code.google.com/p/events-on-fire/wiki/Usage"
 * target="_blank">Usage on Google Project Hosting</a>.
 * </p>
 * <p>
 * All references to producers and consumers in this class are weak, unless otherwise noted. Any producer or consumer
 * will get garbage collected, if there is no reference to it outside this class. All checks will be made on identity
 * instead of equality.
 * </p>
 * 
 * @see <a href="http://code.google.com/p/events-on-fire/wiki/Usage" target="_blank">Usage on Google Project Hosting</a>
 * @author Manfred Hantschel
 */
public class Events implements Runnable
{

    /**
     * The runnable for the thread for firing the events
     */
    private static final Events INSTANCE = new Events();

    /**
     * The thread for firing the events
     */
    private static final Thread EVENTS_THREAD;

    /**
     * The thread local variable containing the count for disabled events
     */
    private static final ThreadLocal<Integer> DISABLED = new ThreadLocal<Integer>();

    /**
     * The default maximum number of threads for the executor service
     */
    private static final int DEFAULT_MAXIMUM_NUMBER_OF_THREADS = 4;

    static
    {
        registerStrategy(new EventHandlerAnnotationStrategy());
        registerStrategy(new PooledEventHandlerAnnotationStrategy());

        // initialize swing events (static init)
        new SwingEvents();

        EVENTS_THREAD = new Thread(INSTANCE, "Events Thread");
        EVENTS_THREAD.setDaemon(true);
        EVENTS_THREAD.start();
    }

    /**
     * <p>
     * Binds the specified consumer / listener to the specified instance of class of a producer. The producer may either
     * be an instance or a class reference.
     * </p>
     * <ul>
     * <li>In case of an instance, the consumer / listener is notified, if the specified producer fires the event
     * (producers are compared by identity rather than equality).</li>
     * <li>In case of a class reference, the consumer / listener is notified, if a producer, that fires the event, is an
     * instance of the specified class (e.g. if the specified producer is a <code>Object.class</code> reference, then
     * the consumer get notified by all events it can handle).</li>
     * </ul>
     * <p>
     * The consumer must contain at least one <code>@{@link EventHandler} public void handleEvent(* event)</code>
     * method, otherwise an exception is thrown. Both, the producer and the consumer must have references outside of the
     * Events class. All references within the Events class are weak, so the objects and the binding gets garbage
     * collected if not referenced. Does nothing if the objects are already bonded.
     * 
     * @param producer the instance or class of a producer, mandatory
     * @param consumers one or more consumers / listeners, mandatory
     * @return the producer
     * @throws IllegalArgumentException if the producer or the consumers are null or one of the consumer cannot handle
     *             events
     */
    public static <PRODUCER_TYPE> PRODUCER_TYPE bind(PRODUCER_TYPE producer, Object... consumers)
        throws IllegalArgumentException
    {
        if (producer == null)
        {
            throw new IllegalArgumentException("Producer is null");
        }

        if ((consumers == null) || (consumers.length == 0))
        {
            throw new IllegalArgumentException("Consumers missing");
        }

        for (Object consumer : consumers)
        {
            if (consumer == null)
            {
                throw new IllegalArgumentException("Consumer is null");
            }

            INSTANCE.enqueue(new Action(Type.BIND, producer, consumer));
        }

        return producer;
    }

    /**
     * <p>
     * Unbinds the specified consumer from the specified instance or class of a producer. The producer may either be an
     * instance or a class reference.
     * </p>
     * <ul>
     * <li>In case of an instance, the consumer / listener unbonded from the specified producer (producers are compared
     * by identity rather than equality).</li>
     * <li>In case of a class reference, the consumer / listener unbonded from any producer that is an instance of the
     * specified class (e.g. if the specified producer is a <code>Object.class</code> reference, then the consumer get
     * unbonded from all producers).</li>
     * </ul>
     * <p>
     * Does nothing if the objects are not bonded.
     * </p>
     * 
     * @param producer the instance or class of a producer, mandatory
     * @param consumers one or more consumers / listeners, mandatory
     * @return the producer
     * @throws IllegalArgumentException if the producer or the consumers are null
     */
    public static <PRODUCER_TYPE> PRODUCER_TYPE unbind(PRODUCER_TYPE producer, Object... consumers)
        throws IllegalArgumentException
    {
        if (producer == null)
        {
            throw new IllegalArgumentException("Producer is null");
        }

        if ((consumers == null) || (consumers.length == 0))
        {
            throw new IllegalArgumentException("Consumers missing");
        }

        for (Object consumer : consumers)
        {
            if (consumer == null)
            {
                throw new IllegalArgumentException("Consumer is null");
            }

            INSTANCE.enqueue(new Action(Type.UNBIND, producer, consumer));
        }

        return producer;
    }

    /**
     * <p>
     * Fires the specified event from the specified instance of a producer. Notifies all consumers that are either
     * directly bonded to the producer or that are bonded to the class, any sub-class or any interface of the producer.
     * </p>
     * <p>
     * Calls the appropriate <code>@{@link EventHandler} public void handleEvent(* event)</code> method of all
     * consumers. Does nothing, if events are disabled for the current thread. Does nothing, if there are no consumers
     * bonded to the producer.
     * </p>
     * 
     * @param producer the producer, mandatory
     * @param event the event, mandatory
     * @param tags, optional, can be checked against tags in annotations
     * @throws IllegalArgumentException if the producer or the event is null
     */
    public static <PRODUCER_TYPE> PRODUCER_TYPE fire(PRODUCER_TYPE producer, Object event, String... tags)
        throws IllegalArgumentException
    {
        if (isDisabled())
        {
            return producer;
        }

        if (producer == null)
        {
            throw new IllegalArgumentException("Producer is null");
        }

        if (event == null)
        {
            throw new IllegalArgumentException("Event is null");
        }

        INSTANCE.enqueue(new Action(Type.FIRE, producer, event, tags));

        return producer;
    }

    /**
     * <p>
     * Disables events from the current thread.
     * </p>
     * <p>
     * Make sure to call the enable method by using a finally block! Multiple calls to disable, increase a counter and
     * it is necessary to call enable as often as you have called disable.
     * </p>
     */
    public static void disable()
    {
        Integer count = DISABLED.get();

        if (count != null)
        {
            DISABLED.set(Integer.valueOf(count.intValue() + 1));
        }
        else
        {
            DISABLED.set(Integer.valueOf(1));
        }
    }

    /**
     * Returns true if events from the current thread are disabled.
     * 
     * @return true if disabled
     */
    public static boolean isDisabled()
    {
        Integer count = DISABLED.get();

        return ((count != null) && (count.intValue() > 0));
    }

    /**
     * <p>
     * Enables events from the current thread.
     * </p>
     * <p>
     * It is wise to place call to this method within a finally block. Multiple calls to disable, increase a counter and
     * it is necessary to call enable as often as you have called disable.
     * </p>
     * 
     * @throws IllegalStateException if events from the current thread are not disabled
     */
    public static void enable() throws IllegalStateException
    {
        Integer count = DISABLED.get();

        if ((count == null) || (count.intValue() <= 0))
        {
            throw new IllegalStateException("Events not disabled");
        }

        DISABLED.set(Integer.valueOf(count - 1));
    }

    /**
     * Adds an event handler invocation to the pooled threads
     * 
     * @param runnable the runnable to be invoked
     */
    static void invokeLater(Runnable runnable)
    {
        INSTANCE.executorService.execute(runnable);
    }

    /**
     * Registers a strategy for event handlers. The strategy decides for a method of a consumer, whether it is capable
     * of handling events or not. By default there exist strategies for following annotations: {@link EventHandler},
     * {@link PooledEventHandler} and {@link SwingEventHandler}.
     * 
     * @param strategy the strategy, never null
     */
    public static void registerStrategy(EventHandlerStrategy strategy)
    {
        if (strategy == null)
        {
            throw new IllegalArgumentException("Strategy is null");
        }

        synchronized (INSTANCE.strategies)
        {
            INSTANCE.strategies.add(strategy);
        }
    }

    /**
     * Scans the consumer class using all registered {@link EventHandlerStrategy}s.
     * 
     * @param type the type of the consumer
     * @return a collection of {@link EventHandlerInfo}s
     */
    static Collection<EventHandlerInfo> scanConsumer(Class<?> type)
    {
        Collection<EventHandlerInfo> results = new LinkedHashSet<EventHandlerInfo>();

        synchronized (INSTANCE.strategies)
        {
            for (EventHandlerStrategy strategy : INSTANCE.strategies)
            {
                strategy.scan(results, type);
            }
        }

        return results;
    }

    /**
     * Returns the executor service for event handler that are executed using pooled threads. The default value is a
     * fixed thread pool using a maximum of 4 threads.
     * 
     * @return the executor service
     */
    public static ExecutorService getExecutorService()
    {
        return INSTANCE.executorService;
    }

    /**
     * Sets the executor service for event handlers that are executed using pooled threads.
     * 
     * @param executorService the executor service, mandatory
     * @throws IllegalArgumentException if the executor service is null
     */
    public static void setExecutorService(ExecutorService executorService)
    {
        if (executorService == null)
        {
            throw new IllegalArgumentException("Executor service is null");
        }

        INSTANCE.executorService = executorService;
    }

    /**
     * Returns the error handler, the {@link DefaultErrorHandler} if not specified.
     * 
     * @return the error handler, never null
     */
    public static ErrorHandler getErrorHandler()
    {
        return INSTANCE.errorHandler;
    }

    /**
     * Sets the error handler.
     * 
     * @param errorHandler the error handler, mandatory
     * @throws IllegalArgumentException if the error handler is null
     */
    public static void setErrorHandler(ErrorHandler errorHandler) throws IllegalArgumentException
    {
        if (errorHandler == null)
        {
            throw new IllegalArgumentException("Error handler is null");
        }

        INSTANCE.errorHandler = errorHandler;
    }

    /**
     * The queue containing actions which wait to get executed.
     */
    private final BlockingQueue<Action> actions;

    /**
     * A map containing all {@link ProducerInfo} objects containing the consumers by the producers.
     */
    private final Map<Reference<Object>, ProducerInfo> producerInfos;

    /**
     * The reference queue for all weak references used to get rid of them if the object has been garbage collected.
     */
    private final ReferenceQueue<Object> referenceQueue;

    /**
     * Holds all strategies for event handler methods
     */
    private final Collection<EventHandlerStrategy> strategies;

    /**
     * The thread pool for pending handler invocations.
     */
    private ExecutorService executorService;

    /**
     * Handler used for logging.
     */
    private ErrorHandler errorHandler;

    private Events()
    {
        super();

        actions = new LinkedBlockingQueue<Action>();
        producerInfos = new ConcurrentHashMap<Reference<Object>, ProducerInfo>();
        referenceQueue = new ReferenceQueue<Object>();

        strategies = new HashSet<EventHandlerStrategy>();

        executorService = Executors.newFixedThreadPool(DEFAULT_MAXIMUM_NUMBER_OF_THREADS);
        errorHandler = new DefaultErrorHandler();
    }

    /**
     * Adds an action to the pending actions
     * 
     * @param action the action
     */
    private void enqueue(Action action)
    {
        actions.add(action);
    }

    /**
     * Worker for pending actions. There is no need to call this method.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            while (true)
            {
                try
                {
                    Action action = actions.take();

                    switch (action.getType())
                    {
                        case FIRE:
                            executeFireAction(action);
                            break;

                        case BIND:
                            executeBindAction(action);
                            break;

                        case UNBIND:
                            executeUnbindAction(action);
                            break;
                    }

                    cleanupReferences();
                }
                catch (InterruptedException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    errorHandler.unhandledException("Exception in event thread", e);
                }
            }
        }
        catch (InterruptedException e)
        {
            errorHandler.interrupted(e);
        }
    }

    /**
     * Fires an event from the producer
     * 
     * @param action the action
     */
    private void executeFireAction(Action action)
    {
        Object producer = action.getProducer();
        Object parameter = action.getParameter();
        String[] tags = action.getTags();
        Class<?> producersClass;
        
        if (!(producer instanceof Class))
        {
            executeFireAction(new WeakIdentityReference<Object>(producer, referenceQueue), producer, parameter, tags);
            
            producersClass = producer.getClass();
        }
        else {
           producersClass = (Class<?>) producer;
        }

        executeFireAction(new WeakIdentityReference<Object>(producersClass, referenceQueue), producer, parameter, tags);

        for (Class<?> producersInterface : producersClass.getInterfaces())
        {
            executeFireAction(new WeakIdentityReference<Object>(producersInterface, referenceQueue), producer,
                parameter, tags);
        }

        producersClass = producersClass.getSuperclass();

        while (producersClass != null)
        {
            executeFireAction(new WeakIdentityReference<Object>(producersClass, referenceQueue), producer, parameter, tags);
            producersClass = producersClass.getSuperclass();
        }
    }

    private void executeFireAction(Reference<Object> producerReference, Object producer, Object parameter, String[] tags)
    {
        ProducerInfo producerInfo = producerInfos.get(producerReference);

        if (producerInfo != null)
        {
            producerInfo.fire(producer, parameter, tags);
        }
    }

    /**
     * Binds a consumer to a producer
     * 
     * @param action the action
     */
    private void executeBindAction(Action action)
    {
        Reference<Object> producerReference = new WeakIdentityReference<Object>(action.getProducer(), referenceQueue);
        Reference<Object> consumerReference = new WeakIdentityReference<Object>(action.getParameter(), referenceQueue);

        ProducerInfo producerInfo = producerInfos.get(producerReference);

        if (producerInfo == null)
        {
            producerInfo = new ProducerInfo();

            producerInfos.put(producerReference, producerInfo);
        }

        producerInfo.add(consumerReference);
    }

    /**
     * Unbinds a consumer form a producer
     * 
     * @param action the action
     */
    private void executeUnbindAction(Action action)
    {
        Object producer = action.getProducer();
        Reference<Object> consumerReference = new WeakIdentityReference<Object>(action.getParameter(), referenceQueue);

        if (producer instanceof Class)
        {
            for (Map.Entry<Reference<Object>, ProducerInfo> entry : producerInfos.entrySet())
            {
                Object current = entry.getKey().get();

                if (current instanceof Class)
                {
                    if (((Class<?>) producer).isAssignableFrom((Class<?>) current))
                    {
                        entry.getValue().remove(consumerReference);
                    }
                }
                else if (((Class<?>) producer).isInstance(current))
                {
                    entry.getValue().remove(consumerReference);
                }
            }
        }
        else
        {
            Reference<Object> producerReference = new WeakIdentityReference<Object>(producer, referenceQueue);
            ProducerInfo producerInfo = producerInfos.get(producerReference);

            if (producerInfo != null)
            {
                producerInfo.remove(consumerReference);
            }
        }
    }

    /**
     * Uses the reference queue to clean up unused references to garbage collected objects
     */
    private void cleanupReferences()
    {
        Reference<?> reference;

        while ((reference = referenceQueue.poll()) != null)
        {
            Iterator<Entry<Reference<Object>, ProducerInfo>> it = producerInfos.entrySet().iterator();

            while (it.hasNext())
            {
                Entry<Reference<Object>, ProducerInfo> entry = it.next();
                Reference<Object> producerReference = entry.getKey();

                if (producerReference == reference)
                {
                    it.remove();
                    break;
                }

                ProducerInfo producer = entry.getValue();

                producer.remove(reference);

                if (producer.isEmpty())
                {
                    it.remove();
                }
            }
        }
    }
}
