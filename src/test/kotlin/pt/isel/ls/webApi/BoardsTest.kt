package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BoardsTest {
    private val services = TasksServices(MemBoardsData, MemUsersData, MemListsData, MemCardsData)

    private val api = WebApi(services)

    private val app = api.authFilter.then(
        routes(
            "boards/" bind Method.GET to api::getBoards,
            "boards/{id}" bind Method.GET to api::getBoard,
            "boards/" bind Method.POST to api::createBoard,
            "boards/{id}/user-list" bind Method.GET to api::getBoardUsers,
            "boards/{id}/user-list/{uid}" bind Method.PUT to api::addUsersOnBoard,
//        "boards/{id}/user-list/" bind Method.POST to api::alterUsersOnBoard,
            "boards/{id}/user-list/{uid}" bind Method.DELETE to api::deleteUserFromBoard
        )
    )

    private fun createUser(name: String = "Maria", email: String = "maria@example.org"): OutputUserDto {
        val newUser = InputUserDto(name, email)
        return services.users.createUser(newUser)
    }

    @BeforeTest
    fun clearStorage() {
        MemDataSource.clearStorage()
    }

    @Test
    fun createBoard() {
        val user = createUser()

        val createDto = InputBoardDto("New Board", "A really cool new board")
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdBoard = Json.decodeFromString<OutputIdDto>(response.bodyString())
        assert(createdBoard.id > 0)
    }

    @Test
    fun getBoard() {
        val user = createUser()

        val createDto = InputBoardDto("New Board", "A really cool new board")
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdBoard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val gottenBoard = Json.decodeFromString<Board>(responseGetBoard.bodyString())
        assertEquals(createDto.name, gottenBoard.name)
        assertEquals(createDto.description, gottenBoard.description)
    }

    @Test
    fun getUserBoards() {
        val user = createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val response1 = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto1))
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, response1.status)

        val response2 = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto2))
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, response2.status)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())
        assertEquals(2, boards.size)
        assertNotNull(boards.firstOrNull { it.name == createDto1.name })
        assertNotNull(boards.firstOrNull { it.name == createDto2.name })
    }

    @Test
    fun getBoardUserList() {
        val user = createUser()

        val createDto = InputBoardDto("New Board", "A really cool new board")
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        val createdBoard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<User>>(responseGetBoard.bodyString())
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
    }

    @Test
    fun addBoardUser() {
        val user = createUser()
        val user2 = createUser("Jose", "jose@example.org")

        val createDto = InputBoardDto("New Board", "A really cool new board")
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        val createdBoard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        val responseAddUser = app(
            Request(
                Method.PUT,
                "boards/${createdBoard.id}/user-list/${user2.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseAddUser.status)

        val responseGetBoardUsers = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        val boardUsers = Json.decodeFromString<List<User>>(responseGetBoardUsers.bodyString())
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun removeBoardUser() {
        val user = createUser()
        val user2 = createUser("Jose", "jose@example.org")

        val createDto = InputBoardDto("New Board", "A really cool new board")
        val response = app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        val createdBoard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        val responseAddUser = app(
            Request(
                Method.PUT,
                "boards/${createdBoard.id}/user-list/${user2.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseAddUser.status)

        val responseGetBoardUsers = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        val boardUsers = Json.decodeFromString<List<User>>(responseGetBoardUsers.bodyString())
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user2.id })

        val responseRemoveUser = app(
            Request(
                Method.DELETE,
                "boards/${createdBoard.id}/user-list/${user2.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseRemoveUser.status)

        val responseGetBoardUsersRemoved = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        val boardUser = Json.decodeFromString<List<User>>(responseGetBoardUsersRemoved.bodyString())
        assertNotNull(boardUser.firstOrNull { it.id == user.id })
        assertNull(boardUser.firstOrNull { it.id == user2.id })
    }
}
