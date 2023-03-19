package pt.isel.ls.http

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.path
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.server.User
import pt.isel.ls.tasksServices.TasksServices





class WebApi{
    private val services = TasksServices()
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
        val email = request.query("email")
        val name = request.query("name")
        checkNotNull(email)
        checkNotNull(name)
        val rsp = services.users.createUser(email,name)
        val responseCode = when(rsp.code){
            1-> CREATED
            2-> BAD_REQUEST
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
                        .body(Json.encodeToString(userResponse.first))
                        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
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

