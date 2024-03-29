package pt.isel.ls.data.mem

import pt.isel.ls.TaskAppException
import pt.isel.ls.data.CardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection

object MemCardsData : CardsData {
    override fun getByList(boardId: Int, listId: Int, limit: Int, skip: Int, connection: Connection?): List<Card> {
        val cards = MemDataSource.cards.filter { it.listId == listId && it.boardId == boardId }

        if (skip > cards.lastIndex) return emptyList()

        return cards.subList(
            skip,
            if (skip + limit <= cards.lastIndex) skip + limit else cards.lastIndex + 1
        )
    }

    override fun add(newCard: InputCardDto, boardId: Int, listId: Int?, connection: Connection?): Card {
        if (!MemDataSource.boards.any { it.id == boardId }) {
            throw TaskAppException(ErrorCodes.BOARD_READ_FAIL)
        }
        if (listId != null) {
            if (!MemDataSource.lists.any { it.id == listId }) {
                throw TaskAppException(ErrorCodes.LIST_READ_FAIL)
            }
        }
        val newId = if (MemDataSource.cards.isEmpty()) 1 else MemDataSource.cards.maxOf { it.id } + 1

        val ts = newCard.dueDate

        val card = Card(
            newId,
            newCard.name,
            newCard.description,
            ts,
            listId,
            boardId,
            0
        )

        MemDataSource.cards.add(card)
        return card
    }

    override fun edit(editCardDto: EditCardDto, boardId: Int, cardId: Int, connection: Connection?) {
        val oldCard = MemDataSource.cards.firstOrNull { it.id == cardId }
            ?: throw TaskAppException(ErrorCodes.CARD_READ_FAIL)
        val newCard = Card(
            oldCard.id,
            editCardDto.name,
            editCardDto.description,
            editCardDto.dueDate ?: oldCard.dueDate,
            oldCard.listId,
            boardId,
            0
        )
        MemDataSource.cards.remove(oldCard)
        MemDataSource.cards.add(newCard)
    }

    override fun getByBoard(board: Board, connection: Connection?): List<Card> =
        MemDataSource.cards.filter { it.boardId == board.id }

    override fun move(inputList: InputMoveCardDto, boardId: Int, cardId: Int, connection: Connection?) {
        if (inputList.cix < 0) throw TaskAppException(ErrorCodes.CARD_MOVE_NEGATIVE)
        val oldCard = MemDataSource.cards.firstOrNull { it.id == cardId && it.boardId == boardId }
            ?: throw TaskAppException(ErrorCodes.CARD_READ_FAIL)

        val newCard = oldCard.copy(listId = inputList.lid)

        MemDataSource.cards.remove(oldCard)
        MemDataSource.cards.add(newCard)
    }

    override fun getById(id: Int, connection: Connection?): Card =
        MemDataSource.cards.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.CARD_READ_FAIL)

    override fun delete(id: Int, connection: Connection?) {
        val card = MemDataSource.cards.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.CARD_READ_FAIL)
        MemDataSource.cards.remove(card)
    }

    override fun exists(id: Int, connection: Connection?): Boolean =
        MemDataSource.cards.any { it.id == id }
}
