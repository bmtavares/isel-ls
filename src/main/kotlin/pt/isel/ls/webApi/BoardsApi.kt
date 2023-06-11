package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityAlreadyExistsException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.User
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.UrlUtils
import java.lang.IllegalStateException

class BoardsApi(
    private val services: TasksServices
) {
    fun getBoard(contexts: RequestContexts): HttpHandler = { request ->
        try {
            val boardId = request.path("id")?.toInt()

            checkNotNull(boardId)
            val user: User? = contexts[request]["user"]
            checkNotNull(user)
            try {
                val board = services.boards.getBoard(boardId, user)
                Response(OK)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(Json.encodeToString(board))
            } catch (e: EntityNotFoundException) {
                Response(NOT_FOUND)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            }
        } catch (e: NumberFormatException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body("Input is not a number")
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createBoard(contexts: RequestContexts): HttpHandler = { request ->
        val user: User? = contexts[request]["user"]
        checkNotNull(user)
        try {
            val board = Json.decodeFromString<InputBoardDto>(request.bodyString())
            if ((board.name.isEmpty()) or (board.name == "")) {
                Response(BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body("Name is mandatory")
            }
            try {
                val rsp = services.boards.createBoard(board, user)
                val returnValue = OutputIdDto(rsp.id)
                Response(CREATED).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(Json.encodeToString(returnValue))
            } catch (e: EntityAlreadyExistsException) {
                Response(BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body("That name is already in use")
            }
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun getBoards(contexts: RequestContexts): HttpHandler = { request ->
        val user: User? = contexts[request]["user"]
        val search = request.query("search")
        checkNotNull(user)
        try {
            val boards = services.boards.getUserBoards(user, search, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(boards))
        } catch (e: DataException) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun getBoardUsers(contexts: RequestContexts): HttpHandler = { request ->
        try {
            val boardId = request.path("id")?.toInt()
            val user: User? = contexts[request]["user"]
            if ((user == null) or (boardId == null)) {
                Response(BAD_REQUEST)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            } else {
                val users = services.boards.getUsersOnBoard(boardId!!, user!!, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
                Response(OK)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(Json.encodeToString(users))
            }
        } catch (e: NumberFormatException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body("Input is not a number")
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun deleteUserFromBoard(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val userId = request.path("uid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(userId) { "User Id must not be null" }
        return try {
            services.boards.deleteUserOnBoard(boardId, userId)
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: DataException) {
            Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: IllegalStateException) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun addUsersOnBoard(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val userId = request.path("uid")?.toInt()
        if ((boardId == null)or (userId == null)) {
            return Response(BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString("Board and user must not be null"))
        } else {
            return try {
                services.boards.addUserOnBoard(boardId!!, userId!!)
                Response(OK)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            } catch (e: DataException) {
                Response(BAD_REQUEST)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(Json.encodeToString(e.message))
            } catch (e: Exception) {
                Response(INTERNAL_SERVER_ERROR)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(Json.encodeToString(e.message))
            }
        }
    }
}
