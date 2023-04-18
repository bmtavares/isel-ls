package pt.isel.ls.tasksServices

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto

class ServiceCards(private val cardsRepo: CardsData) {
    fun createCard(newCard: InputCardDto, boardId: Int, boardListId: Int): Card {
        return try {
            cardsRepo.add(newCard, boardId, boardListId)
        } catch (e: Exception) {
            throw DataException("Failed to create the card")
        }
    }

    fun getCardsOnList(boardId: Int, boardListId: Int,limit: Int = 25, skip :Int = 0): List<Card> = try {
        cardsRepo.getByList(boardId, boardListId,limit,skip)
    } catch (e: Exception) {
        throw DataException("Failed to retrieve Cards")
    }

    fun getCard(boardId: Int, cardId: Int): Card = try {
        cardsRepo.getById(cardId)
    } catch (e: Exception) {
        throw DataException("Failed to retrieve List")
    }

    fun editCard(editCard: EditCardDto, boardId: Int, cardId: Int) {
        return try {
            cardsRepo.edit(editCard, boardId, cardId)
        } catch (_: Exception) {
        }
    }

    fun moveCard(moveList: InputMoveCardDto, boardId: Int, cardId: Int) {
        return try {
            cardsRepo.move(moveList, boardId, cardId)
        } catch (_: Exception) {
        }
    }
}
