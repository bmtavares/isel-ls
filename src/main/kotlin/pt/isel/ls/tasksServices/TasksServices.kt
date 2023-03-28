package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.storage.BoardStorage
import pt.isel.ls.storage.DB.DBBoard
import pt.isel.ls.storage.DB.DBUser
import pt.isel.ls.storage.File.FileBoard
import pt.isel.ls.storage.File.FileUser
import pt.isel.ls.storage.UserStorage

class TasksServices(private val boardsRepo : BoardsData,private val usersRepo : UsersData, private val file : Boolean = false) {

    val users = ServiceUsers(usersRepo)
    val boards = ServiceBoards(boardsRepo)

}