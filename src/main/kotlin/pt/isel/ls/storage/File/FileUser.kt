package pt.isel.ls.storage.File;

import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage
import pt.isel.ls.tasksServices.UserResponses

class FileUser: UserStorage {
    override fun createUser(user: User): UserResponses {
        TODO("Not yet implemented")
    }

    override fun getUser(userId: Int): Pair<User?, UserResponses>? {
        TODO("Not yet implemented")
    }
    override fun getUserByToken(token: String): Pair<User?, UserResponses>? {
        TODO("Not yet implemented")
    }
}