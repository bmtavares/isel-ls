package pt.isel.ls.data.mem

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.entities.User

//object MemCardsData : MemGenericData<Card>(emptyList<Card>() as MutableList<Card>), CardsData {
//
//    private val cardsList = mutableListOf<Card>()
//    override fun getByList(list: BoardList): List<Card> = cardsList.filter { c -> c.listId == list.id }
//
//    override fun getByBoard(board: Board) = cardsList.filter { c -> c.boardId == board.id }
//}
