package pt.isel.ls.tasksServices

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataContext
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto

class ServiceCards(private val context: DataContext, private val cardsRepo: CardsData) {
    fun createCard(newCard: InputCardDto, boardId: Int, boardListId: Int): Card {
        lateinit var card: Card
        try {
            context.handleData { con ->
                card = cardsRepo.add(newCard, boardId, boardListId, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return card
    }

    fun getCardsOnList(boardId: Int, boardListId: Int, limit: Int = 25, skip: Int = 0): List<Card> {
        lateinit var cards: List<Card>
        try {
            context.handleData { con ->
                cards = cardsRepo.getByList(boardId, boardListId, limit, skip, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return cards
    }

    fun getCard(boardId: Int, cardId: Int): Card {
        lateinit var card: Card
        try {
            context.handleData { con ->
                card = cardsRepo.getById(cardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return card
    }

    fun editCard(editCard: EditCardDto, boardId: Int, cardId: Int) {
        try {
            context.handleData { con ->
                cardsRepo.edit(editCard, boardId, cardId, con)
            }
        } catch (_: Exception) {
        }
    }

    fun moveCard(moveList: InputMoveCardDto, boardId: Int, cardId: Int) {
        try {
            context.handleData { con ->
                cardsRepo.move(moveList, boardId, cardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeCard(boardId: Int, cardId: Int) = try {
        context.handleData { con ->
            cardsRepo.delete(cardId, con)
        }
    } catch (ex: DataException) {
        throw ex
    }
}
