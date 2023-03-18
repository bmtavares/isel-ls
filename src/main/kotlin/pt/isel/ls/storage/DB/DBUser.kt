package pt.isel.ls.storage.DB

import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage

class DBUser: UserStorage {
    override fun createUser(user: User) {
        // save the user to a file
    }
}