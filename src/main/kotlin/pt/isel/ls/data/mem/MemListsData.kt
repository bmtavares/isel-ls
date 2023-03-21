package pt.isel.ls.data.mem

import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList

class MemListsData(private val listsList: MutableList<BoardList>) : MemGenericData<BoardList>(listsList), ListsData {
    override fun getListsByBoard(board: Board): List<BoardList> = listsList.filter { l -> l.boardId == board.id }
}
