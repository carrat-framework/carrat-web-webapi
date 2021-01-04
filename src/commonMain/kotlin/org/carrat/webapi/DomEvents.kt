package org.carrat.webapi

/**
 * Turns an event handler function into an [EventListener]
 */
public fun eventHandler(handler: (Event) -> Unit): EventListener {
    return EventListenerHandler(handler)
}

private class EventListenerHandler(private val handler: (Event) -> Unit) : EventListener {
    public override fun handleEvent(e: Event) {
        handler(e)
    }

    public override fun toString(): String = "EventListenerHandler($handler)"
}

public fun mouseEventHandler(handler: (MouseEvent) -> Unit): EventListener {
    return eventHandler { e ->
        if (e is MouseEvent) {
            handler(e)
        }
    }
}
