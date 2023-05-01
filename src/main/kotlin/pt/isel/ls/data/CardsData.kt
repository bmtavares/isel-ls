package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import java.sql.Connection

interface CardsData : Data<Card> {
    fun getByList(boardId: Int, listId: Int, limit: Int = 25, skip: Int = 0, connection: Connection? = null): List<Card>
    fun add(newCard: InputCardDto, boardId: Int, listId: Int?, connection: Connection? = null): Card
    fun edit(editCardDto: EditCardDto, boardId: Int, cardId: Int, connection: Connection? = null)
    fun getByBoard(board: Board, connection: Connection? = null): List<Card>
    fun move(inputList: InputMoveCardDto, boardId: Int, cardId: Int, connection: Connection? = null)
}
