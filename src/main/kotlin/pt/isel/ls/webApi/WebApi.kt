package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityAlreadyExistsException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.User
import pt.isel.ls.http.logRequest
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import java.lang.IllegalStateException

class WebApi(
    private val services: TasksServices
) {
    fun getBoard(contexts: RequestContexts): HttpHandler = { request ->
        logRequest(request)
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
        logRequest(request)
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

    fun getUser(request: Request): Response {
        logRequest(request)
        val userId = request.path("id")?.toInt()
            ?: return Response(BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)

        return try {
            val user = services.users.getUser(userId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(user))
        } catch (e: EntityNotFoundException) {
            Response(NOT_FOUND).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createUser(request: Request): Response {
        logRequest(request)
        val newUser = Json.decodeFromString<InputUserDto>(request.bodyString())
        return try {
            val user = services.users.createUser(newUser)
            Response(CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(user))
        } catch (e: EntityAlreadyExistsException) {
            Response(CONFLICT)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
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

    fun getBoards(contexts: RequestContexts): HttpHandler = { request ->
        logRequest(request)
        val user: User? = contexts[request]["user"]
        val search = request.query("search")
        checkNotNull(user)
        try {
            val boards = services.boards.getUserBoards(user, search, getLimit(request), getSkip(request))
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
        logRequest(request)
        try {
            val boardId = request.path("id")?.toInt()
            val user: User? = contexts[request]["user"]
            if ((user == null) or (boardId == null)) {
                Response(BAD_REQUEST)
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            } else {
                val users = services.boards.getUsersOnBoard(boardId!!, user!!, getLimit(request), getSkip(request))
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

    fun alterUsersOnBoard(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun deleteUserFromBoard(request: Request): Response {
        logRequest(request)
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

    fun getLists(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        return try {
            val boardLists = services.lists.getBoardLists(boardId, getLimit(request), getSkip(request))
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(boardLists))
        } catch (e: DataException) {
            Response(BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createList(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        val listInput = Json.decodeFromString<InputBoardListDto>(request.bodyString())
        return try {
            val boardList = services.lists.createBoardList(boardId, listInput)
            Response(CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(boardList.id)))
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

    fun getLimit(request: Request): Int {
        return try {
            if (Integer.parseInt(request.query("limit").toString()) >= 0) {
                Integer.parseInt(request.query("limit").toString())
            } else {
                25
            }
        } catch (e: java.lang.Exception) {
            25
        }
    }

    fun getSkip(request: Request): Int {
        return try {
            if (Integer.parseInt(request.query("skip").toString()) >= 0) {
                Integer.parseInt(request.query("skip").toString())
            } else {
                0
            }
        } catch (e: java.lang.Exception) { 0 }
    }

    fun getCardsFromList(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        return try {
            val cards = services.cards.getCardsOnList(boardId, boardListId, getLimit(request), getSkip(request))
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(cards))
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

    fun createCard(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        val newCard = Json.decodeFromString<InputCardDto>(request.bodyString())
        return try {
            val card = services.cards.createCard(newCard, boardId, boardListId)
            Response(CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(card.id)))
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

    fun getAllCards(request: Request): Response {
        return TODO("Provide the return value")
    }
    fun deleteList(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        return try {
            services.lists.deleteBoardList(boardListId)
            Response(OK)
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
    fun editList(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        var ncards = request.path("ncards")?.toInt()
        if( ncards == null){
            ncards = 0
        }

        val editList = Json.decodeFromString<EditBoardListDto>(request.bodyString())
        return try {
            services.lists.editBoardList(editList, boardListId, boardId,ncards)
            Response(OK)
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

    fun getList(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        return try {
            val list = services.lists.getBoardList(boardId, boardListId)
            Response(OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(list))
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

    fun alterCardListPosition(request: Request): Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        val cardId = request.path("cid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(cardId) { "List Id must not be null" }
        val inputList = Json.decodeFromString<InputMoveCardDto>(request.bodyString())
        return try {
            services.cards.moveCard(inputList, boardId, cardId)
            Response(OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun moveList(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun addUsersOnBoard(request: Request): Response {
        logRequest(request)
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

    fun getCard(request: Request): Response {
        logRequest(request)
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
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun editCard(request: Request): Response {
        logRequest(request)
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
            Response(INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }
    fun filterUser(contexts: RequestContexts) = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                    val user = services.users.getUserByToken(token)
                    contexts[request]["user"] = user
                    next(request)
                } catch (e: Exception) {
                    when (e) {
                        is DataException -> Response(Status.UNAUTHORIZED).body("Invalid token")
                        else -> Response(Status.INTERNAL_SERVER_ERROR).body("Server Error")
                    }
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }
    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                    services.users.getUserByToken(token)
                    next(request)
                } catch (e: Exception) {
                    when (e) {
                        is DataException -> Response(Status.UNAUTHORIZED).body("Invalid token")
                        else -> Response(Status.INTERNAL_SERVER_ERROR).body("Server Error")
                    }
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }
}
