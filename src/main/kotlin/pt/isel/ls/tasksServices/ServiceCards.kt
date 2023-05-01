package pt.isel.ls.tasksServices

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.pgsql.PgDataContext.handleDB
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto

class ServiceCards(private val cardsRepo: CardsData) {
    fun createCard(newCard: InputCardDto, boardId: Int, boardListId: Int): Card {
        lateinit var card :Card
        try {
            handleDB { con ->
                card = cardsRepo.add(newCard, boardId, boardListId,con)
            }
        } catch (e: Exception) {
            throw DataException("Failed to create the card")
        }
        return card
    }

    fun getCardsOnList(boardId: Int, boardListId: Int, limit: Int = 25, skip: Int = 0): List<Card> {
        lateinit var cards :List<Card>
        try {
            handleDB { con ->
                cards = cardsRepo.getByList(boardId, boardListId, limit, skip, con)
            }
        } catch (e: Exception) {
            throw DataException("Failed to retrieve Cards")
        }
        return cards
    }


    fun getCard(boardId: Int, cardId: Int): Card {
        lateinit var card: Card
        try {
            handleDB { con ->
               card = cardsRepo.getById(cardId, con)
            }
        } catch (e: Exception) {
            throw DataException("Failed to retrieve List")
        }
        return card
    }

    fun editCard(editCard: EditCardDto, boardId: Int, cardId: Int) {
        try {
            handleDB { con ->
                cardsRepo.edit(editCard, boardId, cardId,con)
                }
            } catch (_: Exception) {
        }
    }

    fun moveCard(moveList: InputMoveCardDto, boardId: Int, cardId: Int) {
       try {
           handleDB { con ->
               cardsRepo.move( moveList,boardId,cardId,con)
           }
       }catch (e:Exception){
           throw e
       }
    }

    fun removeCard(boardId: Int, cardId: Int) = try {
            handleDB { con ->
        cardsRepo.delete(cardId,con)
        }
    } catch (ex: DataException) {
        throw ex
    }
}
