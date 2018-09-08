package checkers.tabi_idea

class EventManager {
    var eventList = mutableListOf<Event>()

    fun add(event: Event) {
        eventList.add(eventList.size, event)
    }
}