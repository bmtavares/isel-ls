package pt.isel.ls.storage
import pt.isel.ls.server.User

interface UserStorage {
    fun createUser(user: User)
}