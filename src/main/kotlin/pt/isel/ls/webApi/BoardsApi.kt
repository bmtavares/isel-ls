package pt.isel.ls.webApi

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.TaskAppException
import pt.isel.ls.data.entities.User
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.ErrorCodes
import pt.isel.ls.utils.UrlUtils
import java.lang.IllegalStateException
import java.lang.NumberFormatException

class BoardsApi(
    private val services: TasksServices
) {
    fun getBoard(contexts: RequestContexts): HttpHandler = {
        val boardId = try {
            val boardId = it.path("id")?.toInt()
            checkNotNull(boardId) { "Board ID not provided in URL." }
        } catch (ex: IllegalStateException) { throw TaskAppException(ErrorCodes.URL_PATH_ERROR, issue = ex.message) } catch (ex: NumberFormatException) { throw TaskAppException(ErrorCodes.URL_PATH_TYPE_ERROR) }

        val user: User = contexts[it]["user"] ?: throw TaskAppException(ErrorCodes.NOT_AUTHENTICATED)

        val board = services.boards.getBoard(boardId, user)
        Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(board))
    }

    fun createBoard(contexts: RequestContexts): HttpHandler = {
        val user: User = contexts[it]["user"] ?: throw TaskAppException(ErrorCodes.NOT_AUTHENTICATED)

        try {
            val board = Json.decodeFromString<InputBoardDto>(it.bodyString())
            if (board.name.isEmpty()) throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, "Name is mandatory.")

            val rsp = services.boards.createBoard(board, user)
            val returnValue = OutputIdDto(rsp.id)
            Response(CREATED).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(returnValue))
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }

    fun getBoards(contexts: RequestContexts): HttpHandler = {
        val user: User = contexts[it]["user"] ?: throw TaskAppException(ErrorCodes.NOT_AUTHENTICATED)

        val search = it.query("search")

        val boards = services.boards.getUserBoards(user, search, UrlUtils.getLimit(it), UrlUtils.getSkip(it))
        Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(boards))
    }

    fun getBoardUsers(contexts: RequestContexts): HttpHandler = {
        val boardId = UrlUtils.getPathInt(it, "id", "Board")

        val user: User = contexts[it]["user"] ?: throw TaskAppException(ErrorCodes.NOT_AUTHENTICATED)

        val users = services.boards.getUsersOnBoard(boardId, user, UrlUtils.getLimit(it), UrlUtils.getSkip(it))
        Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(users))
    }

    fun deleteUserFromBoard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val userId = UrlUtils.getPathInt(request, "uid", "User")

        services.boards.deleteUserOnBoard(boardId, userId)
        return Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
    }

    fun addUsersOnBoard(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val userId = UrlUtils.getPathInt(request, "uid", "User")

        services.boards.addUserOnBoard(boardId, userId)
        return Response(OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
    }
}
