package pt.isel.ls.data.mem

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import java.sql.Timestamp

object MemCardsData : CardsData {
    override fun getByList(list: BoardList): List<Card> =
        MemDataSource.cards.filter { it.listId == list.id }

    override fun add(newCard: InputCardDto, boardId: Int, listId: Int?): Card {
        if (!MemDataSource.boards.any { it.id == boardId }) {
            throw EntityNotFoundException(
                "Board does not exist.",
                Card::class
            )
        }
        if (listId != null) {
            if (!MemDataSource.lists.any { it.id == listId }) {
                throw EntityNotFoundException(
                    "List does not exist.",
                    Card::class
                )
            }
        }
        val newId = if (MemDataSource.cards.isEmpty()) 1 else MemDataSource.cards.maxOf { it.id } + 1

        val ts = if (newCard.dueDate != null)
        // Requires that timestamp string is in JDBC format yyyy-m[m]-d[d] hh:mm:ss[.f…]
            Timestamp.valueOf(newCard.dueDate)
        else null

        val card = Card(
            newId,
            newCard.name,
            newCard.description,
            ts,
            listId,
            boardId
        )

        MemDataSource.cards.add(card)
        return card
    }

    override fun edit(editCardDto: EditCardDto, boardId: Int, cardId: Int) {
        val oldCard = MemDataSource.cards.firstOrNull { it.id == cardId }
            ?: throw EntityNotFoundException("Card not found.", Card::class)
        val newCard = Card(
            oldCard.id,
            editCardDto.name,
            editCardDto.description,
            editCardDto.dueDate,
            editCardDto.listId,
            boardId
        )
        MemDataSource.cards.remove(oldCard)
        MemDataSource.cards.add(newCard)
    }

    override fun getByBoard(board: Board): List<Card> =
        MemDataSource.cards.filter { it.boardId == board.id }

    override fun getById(id: Int): Card =
        MemDataSource.cards.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "Card not found.",
            Card::class
        )

    override fun delete(id: Int) {
        val card = MemDataSource.cards.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "Card not found.",
            Card::class
        )
        MemDataSource.cards.remove(card)
    }

    override fun exists(id: Int): Boolean =
        MemDataSource.cards.any { it.id == id }
}
