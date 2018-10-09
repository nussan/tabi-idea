package checkers.tabi_idea.manager

import checkers.tabi_idea.data.Event

class EventManager {
    var
            eventList = mutableListOf<Event>()

    fun add(event: Event) {
        eventList.add(eventList.size, event)
    }
}