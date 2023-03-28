package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.jsonObject
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.server.*
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.UserResponses
import java.io.InputStream

class WebApi(val boardsRepo : BoardsData,val DataRepoUsers : UsersData){

    private val services = TasksServices(boardsRepo,DataRepoUsers)
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
        val board = Json.decodeFromString<NewBoard>(request.bodyString())
        check(board.name.isNotEmpty()){ Response(BAD_REQUEST).body("Board name is mandatory") }
        val rsp = services.boards.createBoard(board.name,board.description) ?: return Response(BAD_REQUEST).body("Failed to create board")
        return Response(CREATED).header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body("boardId = $rsp")
    }

    fun getUser(request: Request):Response{
        logRequest(request)
        val userId = request.path("id")?.toInt()
        checkNotNull(userId)
        val rsp = services.users.getUser(userId)
       return if (rsp?.first != null) Response(OK).header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(rsp.first))
        else Response(NOT_FOUND)
    }

    fun createUser(request: Request):Response{
        logRequest(request)
        val newUser = Json.decodeFromString<NewUser>(request.bodyString())
        checkNotNull(newUser.email)
        checkNotNull(newUser.name)
        val rsp = services.users.createUser(newUser.email,newUser.name)
        val responseCode = when(rsp.code){
            UserResponses.Created.code-> CREATED
            UserResponses.InvalidUser.code-> BAD_REQUEST
            else -> INTERNAL_SERVER_ERROR
        }
        return Response(responseCode)
            .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(rsp.desc))
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

    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                val userResponse = services.users.getUserByToken(token)
                if (userResponse?.first != null) {
                    next(request
                        .header("User",Json.encodeToString(userResponse.first))
                    )
                } else {
                    Response(Status.UNAUTHORIZED).body("Invalid token")
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }
}

