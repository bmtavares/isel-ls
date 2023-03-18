package pt.isel.ls.tasksServices

import pt.isel.ls.storage.DB.DBUser
import pt.isel.ls.storage.File.FileUser
import pt.isel.ls.storage.UserStorage

class TasksServices(private val file : Boolean = false) {
    private val userRepository: UserStorage = if(file){FileUser()}else{DBUser()}

    val users = ServiceUsers(userRepository)

}