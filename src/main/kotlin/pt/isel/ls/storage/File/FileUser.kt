package pt.isel.ls.storage.File;

import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage

class FileUser: UserStorage {
    override fun createUser(user: User) {
        // save the user to a file
    }
}