package com.alawar.common.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class EventBus {
    private static EventBus inst;
    Handlers handlers;

    private static EventBus instance() {
        if (inst == null) {
            inst = new EventBus();
        }
        return inst;
    }

    class Handlers {
        private Map<Class, List<EventHandler>> allHandlers = new LinkedHashMap();

        Handlers() {
        }

        public <E extends Event, H extends EventHandler<E>> HandlerRegistration addHandler(final Class<? extends E> eventClass, final H handler) {
            List<EventHandler> eventHandlers = getEventHandlers(eventClass);
            eventHandlers.add(handler);
            return new HandlerRegistration() { // from class: com.alawar.common.event.EventBus.Handlers.1
                @Override // com.alawar.common.event.HandlerRegistration
                public void removeHandler() {
                    List<EventHandler> handlers = (List) Handlers.this.allHandlers.get(eventClass);
                    if (handlers != null) {
                        handlers.remove(handler);
                    }
                }
            };
        }

        private List<EventHandler> getEventHandlers(Class eventClass) {
            List<EventHandler> eventHandlers = this.allHandlers.get(eventClass);
            if (eventHandlers == null) {
                synchronized (this) {
                    if (eventHandlers == null) {
                        eventHandlers = Collections.synchronizedList(new ArrayList());
                        this.allHandlers.put(eventClass, eventHandlers);
                    }
                }
            }
            return eventHandlers;
        }

        public Iterable<? extends Map.Entry<Class, List<EventHandler>>> entrySet() {
            return this.allHandlers.entrySet();
        }
    }

    private Handlers queryHandlers() {
        if (this.handlers == null) {
            this.handlers = new Handlers();
        }
        return this.handlers;
    }

    public <E extends Event, H extends EventHandler<E>> HandlerRegistration addHandlerInternal(Class<? extends E> eventClass, H handler) {
        return queryHandlers().addHandler(eventClass, handler);
    }

    private void internalPublish(Event event) {
        publish(Arrays.asList(event));
    }

    public void publish(Iterable<? extends Event> events) {
        for (Event event : events) {
            if (this.handlers != null) {
                for (Map.Entry<Class, List<EventHandler>> entry : this.handlers.entrySet()) {
                    if (entry.getKey().isAssignableFrom(event.getClass())) {
                        for (EventHandler handler : entry.getValue()) {
                            handler.onEvent(event);
                        }
                    }
                }
            }
        }
    }

    public static void publish(Event event) {
        instance().internalPublish(event);
    }

    public static <E extends Event, H extends EventHandler<E>> HandlerRegistration addHandler(Class<? extends E> eventClass, H handler) {
        return instance().addHandlerInternal(eventClass, handler);
    }

    public static void clear() {
        inst = null;
    }
}
