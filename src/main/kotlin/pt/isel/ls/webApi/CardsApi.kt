package pt.isel.ls.webApi

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import pt.isel.ls.TaskAppException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.ErrorCodes
import pt.isel.ls.utils.UrlUtils

class CardsApi(
    private val services: TasksServices
) {
    fun deleteCard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val cardId = UrlUtils.getPathInt(request, "cid", "Card")

        services.cards.removeCard(boardId, cardId)

        return Response(OK)
    }

    fun alterCardListPosition(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val cardId = UrlUtils.getPathInt(request, "cid", "Card")

        return try {
            val inputList = Json.decodeFromString<InputMoveCardDto>(request.bodyString())

            services.cards.moveCard(inputList, boardId, cardId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }

    fun getCardsFromList(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val boardListId = UrlUtils.getPathInt(request, "lid", "List")

        val cards = services.cards.getCardsOnList(boardId, boardListId, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
        return Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(cards))
    }

    fun createCard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val boardListId = UrlUtils.getPathInt(request, "lid", "List")

        return try {
            val newCard = Json.decodeFromString<InputCardDto>(request.bodyString())

            val card = services.cards.createCard(newCard, boardId, boardListId)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(card.id)))
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }

    fun getCard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val cardId = UrlUtils.getPathInt(request, "cid", "Card")

        val card = services.cards.getCard(boardId, cardId)
        return Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(card))
    }

    fun editCard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val cardId = UrlUtils.getPathInt(request, "cid", "Card")

        return try {
            val editCard = Json.decodeFromString<EditCardDto>(request.bodyString())

            services.cards.editCard(editCard, boardId, cardId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }
}
