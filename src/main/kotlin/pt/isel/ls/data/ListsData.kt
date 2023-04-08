package pt.isel.ls.data

import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

interface ListsData : Data<BoardList> {
    fun getListsByBoard(boardId: Int, limit: Int = 25, skip: Int = 0): List<BoardList>
    fun edit(editName: String, listId: Int, boardId: Int)
    fun add(newBoardList: InputBoardListDto, boardId: Int): BoardList
}
