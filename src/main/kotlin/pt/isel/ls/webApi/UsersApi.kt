package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.TaskAppException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.ErrorDto
import pt.isel.ls.tasksServices.dtos.LoginUserDto

class UsersApi(
    private val services: TasksServices
) {
    fun loginUser(request: Request): Response =
        try {
            val credentials = Json.decodeFromString<LoginUserDto>(request.bodyString())
            val result = services.users.authenticateUser(credentials)
            Response(Status.OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(result))
        } catch (ex: TaskAppException) {
            Response(ex.status)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(ErrorDto(ex.errorCode.message, ex.errorCode.code)))
        }
}
