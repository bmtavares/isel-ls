package pt.isel.ls.tasksServices

import pt.isel.ls.storage.File.FileUser
import pt.isel.ls.storage.UserStorage

class TasksServices() {
    val userRepository: UserStorage = FileUser()
    val users = ServiceUsers(userRepository)

}