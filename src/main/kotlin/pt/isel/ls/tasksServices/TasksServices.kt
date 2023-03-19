package pt.isel.ls.tasksServices

import pt.isel.ls.storage.BoardStorage
import pt.isel.ls.storage.DB.DBBoard
import pt.isel.ls.storage.DB.DBUser
import pt.isel.ls.storage.file.FileBoard
import pt.isel.ls.storage.file.FileUser
import pt.isel.ls.storage.UserStorage

class TasksServices(private val file : Boolean = false) {
    private val userRepository: UserStorage = if(file){  FileUser()  }else{  DBUser()  }
    private val boardRepository: BoardStorage = if(file){ FileBoard() }else{ DBBoard()  }

    val users = ServiceUsers(userRepository)
    val boards = ServiceBoards(boardRepository)

}