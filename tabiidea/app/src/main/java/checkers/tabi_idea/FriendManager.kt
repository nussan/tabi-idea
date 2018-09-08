package checkers.tabi_idea

class FriendManager {
    var friendList = mutableListOf<Friend>()

    fun add(friend: Friend) {
        friendList.add(friendList.size, friend)
    }
}