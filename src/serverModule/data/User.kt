package serverModule.data

/**
 * Created by kr3v on 04.09.2016.
 * Data class that describes user
 */
data class User(var userId: Int,
                var userVKId: Int?,
                var userFriendsList: Array<Int>?,
                var place: Place = Place(0.0, 0.0)) {
    override fun equals(other: Any?) = other is User && userId == other.userId
    override fun hashCode(): Int{
        return userId
    }
}