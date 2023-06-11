package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import pt.isel.ls.data.DataException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.utils.UrlUtils

class ListsApi(
    private val services: TasksServices
) {
    fun deleteList(request: Request): Response = try {
        val boardId = request.path("id")?.toInt()
        val listId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board ID not provided in URL." }
        checkNotNull(listId) { "List ID not provided in URL." }
        services.lists.removeList(boardId, listId)
        Response(Status.OK)
    } catch (e: DataException) {
        Response(Status.BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(e.message))
    } catch (ex: NumberFormatException) {
        Response(Status.BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    } catch (ex: IllegalStateException) {
        Response(Status.BAD_REQUEST)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    } catch (ex: Exception) {
        Response(Status.INTERNAL_SERVER_ERROR)
            .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
            .body(Json.encodeToString(ex.message))
    }

    fun editList(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        var ncards = request.path("ncards")?.toInt()
        if (ncards == null) {
            ncards = 0
        }

        val editList = Json.decodeFromString<EditBoardListDto>(request.bodyString())
        return try {
            services.lists.editBoardList(editList, boardListId, boardId, ncards)
            Response(Status.OK)
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

    fun getList(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        val boardListId = request.path("lid")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        checkNotNull(boardListId) { "List Id must not be null" }
        return try {
            val list = services.lists.getBoardList(boardId, boardListId)
            Response(Status.OK)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(list))
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

    fun getLists(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        return try {
            val boardLists = services.lists.getBoardLists(boardId, UrlUtils.getLimit(request), UrlUtils.getSkip(request))
            Response(Status.OK).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(boardLists))
        } catch (e: DataException) {
            Response(Status.BAD_REQUEST).header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        } catch (e: Exception) {
            Response(Status.INTERNAL_SERVER_ERROR)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(e.message))
        }
    }

    fun createList(request: Request): Response {
        val boardId = request.path("id")?.toInt()
        checkNotNull(boardId) { "Board Id must not be null" }
        val listInput = Json.decodeFromString<InputBoardListDto>(request.bodyString())
        return try {
            val boardList = services.lists.createBoardList(boardId, listInput)
            Response(Status.CREATED)
                .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                .body(Json.encodeToString(OutputIdDto(boardList.id)))
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
