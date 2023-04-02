package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

interface ListsData : Data<BoardList> {
    fun getListsByBoard(boardId: Int): List<BoardList>

    fun edit(editList: EditBoardListDto,listId:Int)
    fun add(newBoardList: InputBoardListDto,boardId:Int): BoardList
}
