package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices

class ListsApi(
    private val services: TasksServices
) {
    fun deleteList(request: Request): Response = try {
        val boardId = request.path("id")?.toInt()
        val listId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board ID not provided in URL." }
        checkNotNull(listId) { "List ID not provided in URL." }
        services.lists.removeList(boardId, listId)

        Response(Status.OK)
    } catch (ex: NumberFormatException) {
        Response(Status.BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    } catch (ex: Exception) {
        Response(Status.BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    }
}
