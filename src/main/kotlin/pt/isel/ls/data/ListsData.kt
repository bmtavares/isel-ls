package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList

interface ListsData : Data<BoardList> {
    fun getListsByBoard(board: Board): List<BoardList>
}
