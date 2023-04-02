package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto

interface CardsData : Data<Card> {
    fun getByList(boardId: Int,listId: Int): List<Card>

    fun add(newCard:InputCardDto,boardId:Int,listId:Int?):Card

    fun edit(editCardDto: EditCardDto,boardId: Int,cardId:Int)
    fun getByBoard(board: Board): List<Card>

    fun move(inputList:InputMoveCardDto,boardId: Int,cardId:Int)
}
