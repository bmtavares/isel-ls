package pt.isel.ls.tasksServices

import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage

class ServiceUsers(val userRepository: UserStorage) {

    fun createUser(u : User):Pair<Int,String >{
        // created
        // alredy exist
        // not valid email ...
        userRepository.createUser(u);

        return Pair(1,"CREATED")
    }


}