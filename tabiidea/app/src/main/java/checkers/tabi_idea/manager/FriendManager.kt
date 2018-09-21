package checkers.tabi_idea.manager

import checkers.tabi_idea.data.Friend

class FriendManager {
    var friendList = mutableListOf<Friend>()

    fun add(friend: Friend) {
        friendList.add(friendList.size, friend)
    }
}