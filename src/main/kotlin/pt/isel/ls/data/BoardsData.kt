package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import java.sql.Connection

interface BoardsData : Data<Board> {
    fun getByName(name: String, connection: Connection? = null): Board
    fun getUserBoards(user: User, limit: Int = 25, skip: Int = 0, connection: Connection? = null): List<Board>
    fun edit(editBoard: EditBoardDto, connection: Connection? = null)
    fun add(newBoard: InputBoardDto, connection: Connection? = null): Board
    fun addUserToBoard(userId: Int, boardId: Int, connection: Connection? = null)
    fun deleteUserFromBoard(userId: Int, boardId: Int, connection: Connection? = null)
    fun getUsers(boardId: Int, user: User, limit: Int = 25, skip: Int = 0, connection: Connection? = null): List<User>
}
