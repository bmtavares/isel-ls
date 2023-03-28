package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.DataException

import pt.isel.ls.http.logRequest
import pt.isel.ls.server.*
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputEntitiesDto

class WebApi(val boardsRepo : BoardsData,val DataRepoUsers : UsersData){

    private val services = TasksServices(boardsRepo,DataRepoUsers)

val listUser= listOf<User>(User(0,"a","a"), User(0,"a","a"))
    fun getBoard(request: Request):Response {
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId)
        val user = Json.decodeFromString<User>(request.body.toString())
        val board = services.boards.getBoard(boardId,user)
        return Response(OK)
            .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(board))
    }

    fun createBoard(request: Request):Response {
        logRequest(request)
        val user = Json.decodeFromString<User>(request.header("User").toString())
        val board = Json.decodeFromString<InputBoardDto>(request.bodyString())
        check(board.name.isNotEmpty()){ Response(BAD_REQUEST).body("Board name is mandatory") }
        val rsp = services.boards.createBoard(board) ?: return Response(BAD_REQUEST).body("Failed to create board")
        return Response(CREATED).header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body("boardId = $rsp")
    }

    fun getUser(request: Request):Response{
        logRequest(request)
        val userId = request.path("id")?.toInt()
        checkNotNull(userId)
        try {
        val user = services.users.getUser(userId)
        return  Response(OK).header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(user))
        }catch (e:DataException){
            return Response(BAD_REQUEST)
        }
    }

    fun createUser(request: Request):Response{
        logRequest(request)
        val newUser = Json.decodeFromString<InputUserDto>(request.bodyString())
        try {
        val user = services.users.createUser(newUser)
        return Response(CREATED)
            .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(user))
        }catch (e:DataException){
            return Response(BAD_REQUEST)
                .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun getBoardUsers(request: Request):Response{
        logRequest(request)
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId)
        val user = Json.decodeFromString<User>(request.body.toString())
        val users = services.boards.getUsersOnBoard(boardId,user)
        return Response(OK)
            .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(users))
    }

    fun alterUsersOnBoard(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun deleteUsersFromBoard(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun getLists(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun createList(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun getCardsFromList(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun createCard(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun getAllCards(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun editList(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun getList(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun alterListPosition(request: Request): Response {
        return TODO("Provide the return value")
    }

    fun moveList(request: Request): Response {
        return TODO("Provide the return value")
    }

    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                val user = services.users.getUserByToken(token)
                    next(request
                        .header("User",Json.encodeToString(user))
                    )
                }catch (e:Exception){
                    when (e){
                        is DataException -> Response(Status.UNAUTHORIZED).body("Invalid token")
                        else -> Response(Status.INTERNAL_SERVER_ERROR).body("Server Error")
                     }
                }
                }else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }

        }
    }
}



