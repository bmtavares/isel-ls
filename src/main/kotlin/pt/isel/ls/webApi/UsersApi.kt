package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.TaskAppException
import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityAlreadyExistsException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.ErrorDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
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

    fun getUser(request: Request): Response {
        val userId = request.path("id")?.toInt()
            ?: return Response(Status.BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)

        return try {
            val user = services.users.getUser(userId)
            Response(Status.OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(user))
        } catch (e: EntityNotFoundException) {
            Response(Status.NOT_FOUND).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createUser(request: Request): Response {
        val newUser = Json.decodeFromString<InputUserDto>(request.bodyString())
        return try {
            val user = services.users.createUser(newUser)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(user))
        } catch (e: EntityAlreadyExistsException) {
            Response(Status.CONFLICT)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: DataException) {
            Response(Status.BAD_REQUEST)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }
}
