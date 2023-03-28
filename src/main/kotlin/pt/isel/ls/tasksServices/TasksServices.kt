package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData


class TasksServices(private val boardsRepo : BoardsData,private val usersRepo : UsersData, private val file : Boolean = false) {

    val users = ServiceUsers(usersRepo)
    val boards = ServiceBoards(boardsRepo)

}