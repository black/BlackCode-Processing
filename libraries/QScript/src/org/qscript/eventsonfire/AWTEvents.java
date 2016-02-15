package org.qscript.eventsonfire;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AWTEvents
{

    /**
     * An abstract implementation for implementors of listener interfaces. If the producer is not specified, usually the
     * source of the event will be used. If the tags are not specified, the implementing listener will define it.
     * 
     * @author ham
     */
    public static abstract class AbstractListener
    {

        private final Object producer;
        private final String[] tags;

        /**
         * Creates an abstract implementation of the listener interface. If the producer is null, the source of the
         * event will be used as producer. If the tags is null or empty, the implementor will define the tag.
         * 
         * @param producer the producer, may be null
         * @param tags the tags, may be null
         */
        public AbstractListener(Object producer, String... tags)
        {
            super();

            this.producer = producer;
            this.tags = tags;
        }

        protected void fire(Object producerFallback, Object event, String... tagsFallback)
        {
            Object producer = (this.producer != null) ? this.producer : producerFallback;
            String[] tags = ((this.tags != null) && (this.tags.length > 0)) ? this.tags : tagsFallback;

            Events.fire(producer, event, tags);
        }
    }

    /**
     * A custom implementation of the action listener interface using either the specified producer or the source of the
     * event and the specified tags or the action command of the event as tag.
     * 
     * @author ham
     */
    public static class EventsActionListener extends AbstractListener implements ActionListener
    {

        /**
         * Creates an action listener using the specified producer and tags. If the producer is null, the source of the
         * event will be used as producer. If the tags is null or empty, the action command of the event will be used as
         * tag.
         * 
         * @param producer the producer, may be null
         * @param tags the tags, may be null
         */
        public EventsActionListener(Object producer, String... tags)
        {
            super(producer, tags);
        }

        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent event)
        {
            fire(event.getSource(), event, event.getActionCommand());
        }
    }

    /**
     * A custom implementation of the item listener interface using either the specified producer or the source of the
     * event and the specified tags.
     * 
     * @author ham
     */
    public static class EventsItemListener extends AbstractListener implements ItemListener
    {

        /**
         * Creates an item listener using the specified producer and tags. If the producer is null, the source of the
         * event will be used as producer.
         * 
         * @param producer the producer, may be null
         * @param tags the tags, may be null
         */
        public EventsItemListener(Object producer, String... tags)
        {
            super(producer, tags);
        }

        /**
         * {@inheritDoc}
         */
        public void itemStateChanged(ItemEvent event)
        {
            fire(event.getSource(), event);
        }
    }

    /**
     * A custom implementation of the focus listener interface using either the specified producer or the source of the
     * event and the specified tags or the action command of the event as tag. Reacts only to focus gained events.
     * 
     * @author ham
     */
    public static class EventsFocusGainedListener extends AbstractListener implements FocusListener
    {

        /**
         * Creates a focus listener using the specified producer and tags. If the producer is null, the source of the
         * event will be used as producer.
         * 
         * @param producer the producer, may be null
         * @param tags the tags, may be null
         */
        public EventsFocusGainedListener(Object producer, String... tags)
        {
            super(producer, tags);
        }

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent event)
        {
            fire(event.getSource(), event);
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent event)
        {
            // intentionally left blank
        }
    }

    /**
     * A custom implementation of the focus listener interface using either the specified producer or the source of the
     * event and the specified tags or the action command of the event as tag. Reacts only to focus lost events.
     * 
     * @author ham
     */
    public static class EventsFocusLostListener extends AbstractListener implements FocusListener
    {

        /**
         * Creates a focus listener using the specified producer and tags. If the producer is null, the source of the
         * event will be used as producer.
         * 
         * @param producer the producer, may be null
         * @param tags the tags, may be null
         */
        public EventsFocusLostListener(Object producer, String... tags)
        {
            super(producer, tags);
        }

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent event)
        {
            // intentionally left blank
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent event)
        {
            fire(event.getSource(), event);
        }
    }

    private static final ActionListener DEFAULT_ACTION_LISTENER = new EventsActionListener(null);
    private static final ItemListener DEFAULT_ITEM_LISTENER = new EventsItemListener(null);
    private static final FocusListener DEFAULT_FOCUS_GAINED_LISTENER = new EventsFocusGainedListener(null);
    private static final FocusListener DEFAULT_FOCUS_LOST_LISTENER = new EventsFocusLostListener(null);

    /**
     * Returns an action listener, that fires an event using the source of the event as producer and the action command
     * as tag.
     * 
     * @return the action listener
     */
    public static ActionListener fireOnAction()
    {
        return DEFAULT_ACTION_LISTENER;
    }

    /**
     * Creates an action listener, that fires an event using the specified producer and the specified tags. If the
     * producer is null, the source of the event will be used as producer. If the tags are null or empty, the action
     * command will be used as tag.
     * 
     * @param producer the producer
     * @param tags the tags
     * @return the action listener
     */
    public static ActionListener fireOnAction(Object producer, String... tags)
    {
        return new EventsActionListener(producer, tags);
    }

    /**
     * Returns an item listener, that fires an event using the source of the event as producer.
     * 
     * @return the item listener
     */
    public static ItemListener fireOnItemChanged()
    {
        return DEFAULT_ITEM_LISTENER;
    }

    /**
     * Creates an item listener, that fires an event using the specified producer and the specified tags. If the
     * producer is null, the source of the event will be used as producer.
     * 
     * @param producer the producer
     * @param tags the tags
     * @return the item listener
     */
    public static ItemListener fireOnItemChanged(Object producer, String... tags)
    {
        return new EventsItemListener(producer, tags);
    }

    /**
     * Returns a focus listener, that fires an event on focus gain using the source of the event as producer.
     * 
     * @return the focus listener
     */
    public static FocusListener fireOnFocusGained()
    {
        return DEFAULT_FOCUS_GAINED_LISTENER;
    }

    /**
     * Creates a focus listener, that fires an event on focus gain using the specified producer and the specified tags.
     * If the producer is null, the source of the event will be used as producer.
     * 
     * @param producer the producer
     * @param tags the tags
     * @return the focus listener
     */
    public static FocusListener fireOnFocusGained(Object producer, String... tags)
    {
        return new EventsFocusGainedListener(producer, tags);
    }

    /**
     * Returns a focus listener, that fires an event on focus lost using the source of the event as producer.
     * 
     * @return the focus listener
     */
    public static FocusListener fireOnFocusLost()
    {
        return DEFAULT_FOCUS_LOST_LISTENER;
    }

    /**
     * Creates a focus listener, that fires an event on focus lost using the specified producer and the specified tags.
     * If the producer is null, the source of the event will be used as producer.
     * 
     * @param producer the producer
     * @param tags the tags
     * @return the focus listener
     */
    public static FocusListener fireOnFocusLost(Object producer, String... tags)
    {
        return new EventsFocusLostListener(producer, tags);
    }

}
