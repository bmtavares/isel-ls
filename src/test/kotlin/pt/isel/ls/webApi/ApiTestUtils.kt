package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto

class ApiTestUtils(api: WebApi, context: RequestContexts) {
    private val app: RoutingHttpHandler

    private val createUserDto: InputUserDto
    private val createBoardDto: InputBoardDto
    private val createListDto: InputBoardListDto

    fun createUser(dto: InputUserDto = createUserDto): OutputUserDto {
        val response = app(
            Request(
                Method.POST,
                "users"
            ).body(Json.encodeToString(dto))
        )
        return Json.decodeFromString(response.bodyString())
    }

    fun createBoard(token: String, dto: InputBoardDto = createBoardDto): OutputIdDto {
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(dto))
                .header("Authorization", "Bearer $token")
        )
        return Json.decodeFromString(response.bodyString())
    }

    fun createList(token: String, boardId: Int, dto: InputBoardListDto = createListDto): OutputIdDto {
        val response = app(
            Request(
                Method.POST,
                "boards/$boardId/lists"
            )
                .body(Json.encodeToString(dto))
                .header("Authorization", "Bearer $token")
        )
        return Json.decodeFromString(response.bodyString())
    }

    init {
        this.app = routes(
            ServerFilters.InitialiseRequestContext(context).then(api.filterUser(context)).then(
                routes(
                    "boards/" bind Method.POST to api.createBoard(context),
                    "boards/{id}/lists" bind Method.POST to api::createList
                )
            ),
            "users" bind Method.POST to api::createUser
        )
        this.createUserDto = InputUserDto("Maria", "maria@example.org", "olamundo")
        this.createBoardDto = InputBoardDto("New Board", "A really cool new board")
        this.createListDto = InputBoardListDto("Cool List")
    }
}
