package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto

interface BoardsData : Data<Board> {
    fun getByName(name: String): Board?
    fun getUserBoards(user: User, limit: Int = 25, skip: Int = 0): List<Board>
    fun edit(editBoard: EditBoardDto)
    fun add(newBoard: InputBoardDto): Board
    fun addUserToBoard(userId: Int, boardId: Int)
    fun deleteUserFromBoard(userId: Int, boardId: Int)
    fun getUsers(boardId: Int, user: User, limit: Int = 25, skip: Int = 0): List<User>
}
