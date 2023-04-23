package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices

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
}
