package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card

interface CardsData : Data<Card> {
    fun getByList(list: BoardList): List<Card>
    fun getByBoard(board: Board): List<Card>
}
