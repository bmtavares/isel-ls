package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto

interface BoardsData : Data<Board> {
    fun getByName(name: String): Board?
    fun getUserBoards(user: User): List<Board>
    fun edit(editBoard: EditBoardDto)
    fun add(inputBoardDto: InputBoardDto): Board
    fun getUsers(boardId:Int,user: User):List<User>
}
