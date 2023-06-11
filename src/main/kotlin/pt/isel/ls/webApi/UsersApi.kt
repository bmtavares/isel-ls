package pt.isel.ls.webApi

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.TaskAppException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.LoginUserDto
import pt.isel.ls.utils.ErrorCodes
import pt.isel.ls.utils.UrlUtils

class UsersApi(
    private val services: TasksServices
) {
    fun loginUser(request: Request): Response {
        return try {
            val credentials = Json.decodeFromString<LoginUserDto>(request.bodyString())
            val result = services.users.authenticateUser(credentials)
            Response(Status.OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(result))
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }

    fun getUser(request: Request): Response {
        val userId = UrlUtils.getPathInt(request, "id", "User")

        val user = services.users.getUser(userId)

        return Response(Status.OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(user))
    }

    fun createUser(request: Request): Response {
        return try {
            val newUser = Json.decodeFromString<InputUserDto>(request.bodyString())

            val user = services.users.createUser(newUser)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(user))
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }
}
