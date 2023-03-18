package pt.isel.ls.storage
import pt.isel.ls.server.User
import pt.isel.ls.tasksServices.UserResponses

interface UserStorage {
    fun createUser(user: User):UserResponses
    fun getUserByToken(token:String):Pair<User?, UserResponses>?

    fun getUser(userId:Int): Pair<User?,UserResponses>?
}