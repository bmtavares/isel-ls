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
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.ErrorCodes
import pt.isel.ls.utils.UrlUtils

class ListsApi(
    private val services: TasksServices
) {
    fun deleteList(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val listId = UrlUtils.getPathInt(request, "lid", "List")

        services.lists.removeList(boardId, listId)
        return Response(Status.OK)
    }

    fun editList(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val boardListId = UrlUtils.getPathInt(request, "lid", "List")

        val ncards = request.path("ncards")?.toInt() ?: 0

        return try {
            val editList = Json.decodeFromString<EditBoardListDto>(request.bodyString())
            services.lists.editBoardList(editList, boardListId, boardId, ncards)
            Response(Status.OK)
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }

    fun getList(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")
        val boardListId = UrlUtils.getPathInt(request, "lid", "List")

        val list = services.lists.getBoardList(boardId, boardListId)
        return Response(Status.OK)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(list))
    }

    fun getLists(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")

        val boardLists = services.lists.getBoardLists(boardId, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
        return Response(Status.OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(boardLists))
    }

    fun createList(request: Request): Response {
        val boardId = UrlUtils.getPathInt(request, "id", "Board")

        return try {
            val listInput = Json.decodeFromString<InputBoardListDto>(request.bodyString())

            val boardList = services.lists.createBoardList(boardId, listInput)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(boardList.id)))
        } catch (ex: SerializationException) { throw TaskAppException(ErrorCodes.JSON_BODY_ERROR, ex.message) }
    }
}
