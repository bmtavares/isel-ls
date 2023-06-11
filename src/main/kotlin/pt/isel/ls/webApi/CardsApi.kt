package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.data.DataException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.UrlUtils

class CardsApi(
    private val services: TasksServices
) {
    fun deleteCard(request: Request): Response = try {
        val boardId = request.path("id")?.toInt()
        val cardId = request.path("cid")?.toInt()
        checkNotNull(boardId) { "Board ID not provided in URL." }
        checkNotNull(cardId) { "Card ID not provided in URL." }
        services.cards.removeCard(boardId, cardId)

        Response(OK)
    } catch (ex: NumberFormatException) {
        Response(BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    } catch (ex: Exception) {
        Response(BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    }

    fun alterCardListPosition(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val cardId = request.path("cid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(cardId) { "List Id must not be null" }
        val inputList = Json.decodeFromString<InputMoveCardDto>(request.bodyString())
        return try {
            services.cards.moveCard(inputList, boardId, cardId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun getCardsFromList(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        return try {
            val cards = services.cards.getCardsOnList(boardId, boardListId, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(cards))
        } catch (e: DataException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createCard(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        val newCard = Json.decodeFromString<InputCardDto>(request.bodyString())
        return try {
            val card = services.cards.createCard(newCard, boardId, boardListId)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(card.id)))
        } catch (e: DataException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun getCard(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val cardId = request.path("cid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(cardId) { "List Id must not be null" }
        return try {
            val card = services.cards.getCard(boardId, cardId)
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(card))
        } catch (e: DataException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun editCard(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val cardId = request.path("cid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(cardId) { "List Id must not be null" }
        val editCard = Json.decodeFromString<EditCardDto>(request.bodyString())
        return try {
            services.cards.editCard(editCard, boardId, cardId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: Exception) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }
}
