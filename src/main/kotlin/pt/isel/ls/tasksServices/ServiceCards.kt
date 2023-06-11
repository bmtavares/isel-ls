package pt.isel.ls.tasksServices

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataContext
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto

class ServiceCards(private val context: DataContext, private val cardsRepo: CardsData) {
    fun createCard(newCard: InputCardDto, boardId: Int, boardListId: Int): Card {
        lateinit var card: Card
        context.handleData { con ->
            card = cardsRepo.add(newCard, boardId, boardListId, con)
        }

        return card
    }

    fun getCardsOnList(boardId: Int, boardListId: Int, limit: Int = 25, skip: Int = 0): List<Card> {
        lateinit var cards: List<Card>
        context.handleData { con ->
            cards = cardsRepo.getByList(boardId, boardListId, limit, skip, con)
        }

        return cards
    }

    fun getCard(boardId: Int, cardId: Int): Card {
        lateinit var card: Card
        context.handleData { con ->
            card = cardsRepo.getById(cardId, con)
        }

        return card
    }

    fun editCard(editCard: EditCardDto, boardId: Int, cardId: Int) {
        context.handleData { con ->
            cardsRepo.edit(editCard, boardId, cardId, con)
        }
    }

    fun moveCard(moveList: InputMoveCardDto, boardId: Int, cardId: Int) {
        context.handleData { con ->
            cardsRepo.move(moveList, boardId, cardId, con)
        }
    }

    fun removeCard(boardId: Int, cardId: Int) {
        context.handleData { con ->
            cardsRepo.delete(cardId, con)
        }
    }
}
