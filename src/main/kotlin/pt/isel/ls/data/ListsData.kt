package pt.isel.ls.data

import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import java.sql.Connection

interface ListsData : Data<BoardList> {
    fun getListsByBoard(boardId: Int, limit: Int = 25, skip: Int = 0, connection: Connection? = null): List<BoardList>
    fun edit(editName: String, listId: Int, boardId: Int, ncards : Int = 0,connection: Connection? = null)
    fun add(newBoardList: InputBoardListDto, boardId: Int, connection: Connection? = null): BoardList
}
