package com.alawar.common.event;

import com.alawar.common.event.Event;

/* loaded from: classes.dex */
public interface EventHandler<E extends Event> {
    void onEvent(E e);
}
