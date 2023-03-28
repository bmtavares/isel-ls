package pt.isel.ls.data.mem

import pt.isel.ls.data.ListsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserToken


//    object MemListsData : MemGenericData<BoardList>(emptyList<BoardList>() as MutableList<BoardList>), ListsData {
//        private val listsList = mutableListOf<BoardList>()
//
//
//        override fun getListsByBoard(board: Board): List<BoardList> = listsList.filter { l -> l.boardId == board.id }
//}
