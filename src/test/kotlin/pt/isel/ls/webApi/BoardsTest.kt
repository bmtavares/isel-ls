package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemDataContext
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import pt.isel.ls.tasksServices.dtos.SecureOutputUserDto
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BoardsTest {
    private val services = TasksServices(MemDataContext, MemBoardsData, MemUsersData, MemListsData, MemCardsData)
    private val filters = Filters(services)
    private val unitApi = BoardsApi(services)
    private val usersApi = UsersApi(services)
    private val listsApi = ListsApi(services)
    private val context = RequestContexts()
    private val prepare = ApiTestUtils(filters, unitApi, usersApi, listsApi, context)

    private val app =
        routes(
            ServerFilters.InitialiseRequestContext(context).then(filters.filterUser(context)).then(
                routes(
                    "boards/{id}" bind Method.GET to unitApi.getBoard(context),
                    "boards/" bind Method.GET to unitApi.getBoards(context),
                    "boards/" bind Method.POST to unitApi.createBoard(context),
                    "boards/{id}/user-list" bind Method.GET to unitApi.getBoardUsers(context)
                )
            ),
            filters.authFilter.then(
                routes(
                    "boards/{id}/user-list/{uid}" bind Method.PUT to unitApi::addUsersOnBoard,
                    "boards/{id}/user-list/{uid}" bind Method.DELETE to unitApi::deleteUserFromBoard
                )
            )
        )

    @BeforeTest
    fun clearStorage() {
        MemDataSource.clearStorage()
    }

    @Test
    fun createBoard() {
        val user = prepare.createUser()

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
        val user = prepare.createUser()

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

    private fun simpleCreateBoard(createData: InputBoardDto, token: String) {
        app(
            Request(
                Method.POST,
                "boards"
            )
                .body(Json.encodeToString(createData))
                .header("Authorization", "Bearer $token")
        ).let {
            assertEquals(Status.CREATED, it.status)
        }
    }

    @Test
    fun getUserBoards() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)

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
    fun getUserBoardsWithOptionalLimit1() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val createDto3 = InputBoardDto("New Board 3", "Ditto")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)
        simpleCreateBoard(createDto3, user.token)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards?limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())

        assertEquals(1, boards.size)
        assertNotNull(boards.firstOrNull { it.name == createDto1.name })
        assertNull(boards.firstOrNull { it.name == createDto2.name })
        assertNull(boards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getUserBoardsWithOptionalLimitOverSize() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val createDto3 = InputBoardDto("New Board 3", "Ditto")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)
        simpleCreateBoard(createDto3, user.token)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards?limit=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())

        assertEquals(3, boards.size)
        assertNotNull(boards.firstOrNull { it.name == createDto1.name })
        assertNotNull(boards.firstOrNull { it.name == createDto2.name })
        assertNotNull(boards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getUserBoardsWithOptionalSkip1() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val createDto3 = InputBoardDto("New Board 3", "Ditto")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)
        simpleCreateBoard(createDto3, user.token)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards?skip=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())

        assertEquals(2, boards.size)
        assertNull(boards.firstOrNull { it.name == createDto1.name })
        assertNotNull(boards.firstOrNull { it.name == createDto2.name })
        assertNotNull(boards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getUserBoardsWithOptionalSkipOverSize() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val createDto3 = InputBoardDto("New Board 3", "Ditto")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)
        simpleCreateBoard(createDto3, user.token)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards?skip=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())

        assertEquals(0, boards.size)
    }

    @Test
    fun getUserBoardsWithOptionalParameters() {
        val user = prepare.createUser()

        val createDto1 = InputBoardDto("New Board", "A really cool new board")
        val createDto2 = InputBoardDto("New Board 2", "Derivative work actually")
        val createDto3 = InputBoardDto("New Board 3", "Ditto")

        simpleCreateBoard(createDto1, user.token)
        simpleCreateBoard(createDto2, user.token)
        simpleCreateBoard(createDto3, user.token)

        val responseGetBoards = app(
            Request(
                Method.GET,
                "boards?skip=1&limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoards.status)
        assertEquals("application/json", responseGetBoards.header("content-type"))
        val boards = Json.decodeFromString<List<Board>>(responseGetBoards.bodyString())

        assertEquals(1, boards.size)
        assertNull(boards.firstOrNull { it.name == createDto1.name })
        assertNotNull(boards.firstOrNull { it.name == createDto2.name })
        assertNull(boards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getBoardUserList() {
        val user = prepare.createUser()

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
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoard.bodyString())
        assertEquals(1, boardUsers.size)
        assertEquals(user.id, boardUsers.first().id)
    }

    private fun simpleAddUserToBoard(token: String, userId: Int, boardId: Int) {
        app(
            Request(
                Method.PUT,
                "boards/$boardId/user-list/$userId"
            )
                .header("Authorization", "Bearer $token")
        ).let {
            assertEquals(Status.OK, it.status)
        }
    }

    @Test
    fun getBoardUserListWithOptionalLimit1() {
        val user = prepare.createUser()
        val user1 = prepare.createUser(InputUserDto("Josephine", "josephine@example.org", "helloworld"))
        val user2 = prepare.createUser(InputUserDto("Jonathan", "jonathan@example.org", "helloworld"))

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

        simpleAddUserToBoard(user.token, user1.id, createdBoard.id)
        simpleAddUserToBoard(user.token, user2.id, createdBoard.id)

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list?limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoard.bodyString())

        assertEquals(1, boardUsers.size)
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
        assertNull(boardUsers.firstOrNull { it.id == user1.id })
        assertNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun getBoardUserListWithOptionalLimitOverSize() {
        val user = prepare.createUser()
        val user1 = prepare.createUser(InputUserDto("Josephine", "josephine@example.org", "helloworld"))
        val user2 = prepare.createUser(InputUserDto("Jonathan", "jonathan@example.org", "helloworld"))

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

        simpleAddUserToBoard(user.token, user1.id, createdBoard.id)
        simpleAddUserToBoard(user.token, user2.id, createdBoard.id)

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list?limit=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoard.bodyString())

        assertEquals(3, boardUsers.size)
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user1.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun getBoardUserListWithOptionalSkip1() {
        val user = prepare.createUser()
        val user1 = prepare.createUser(InputUserDto("Josephine", "josephine@example.org", "helloworld"))
        val user2 = prepare.createUser(InputUserDto("Jonathan", "jonathan@example.org", "helloworld"))

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

        simpleAddUserToBoard(user.token, user1.id, createdBoard.id)
        simpleAddUserToBoard(user.token, user2.id, createdBoard.id)

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list?skip=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoard.bodyString())

        assertEquals(2, boardUsers.size)
        assertNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user1.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun getBoardUserListWithOptionalSkipOverSize() {
        val user = prepare.createUser()
        val user1 = prepare.createUser(InputUserDto("Josephine", "josephine@example.org", "helloworld"))
        val user2 = prepare.createUser(InputUserDto("Jonathan", "jonathan@example.org", "helloworld"))

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

        simpleAddUserToBoard(user.token, user1.id, createdBoard.id)
        simpleAddUserToBoard(user.token, user2.id, createdBoard.id)

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list?skip=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<User>>(responseGetBoard.bodyString())

        assertEquals(0, boardUsers.size)
    }

    @Test
    fun getBoardUserListWithOptionalParameters() {
        val user = prepare.createUser()
        val user1 = prepare.createUser(InputUserDto("Josephine", "josephine@example.org", "helloworld"))
        val user2 = prepare.createUser(InputUserDto("Jonathan", "jonathan@example.org", "helloworld"))

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

        simpleAddUserToBoard(user.token, user1.id, createdBoard.id)
        simpleAddUserToBoard(user.token, user2.id, createdBoard.id)

        val responseGetBoard = app(
            Request(
                Method.GET,
                "boards/${createdBoard.id}/user-list?skip=1&limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.OK, responseGetBoard.status)
        assertEquals("application/json", responseGetBoard.header("content-type"))
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoard.bodyString())

        assertEquals(1, boardUsers.size)
        assertNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user1.id })
        assertNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun addBoardUser() {
        val user = prepare.createUser()
        val user2 = prepare.createUser(InputUserDto("Jose", "jose@example.org", "helloworld"))

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
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoardUsers.bodyString())
        assertNotNull(boardUsers.firstOrNull { it.id == user.id })
        assertNotNull(boardUsers.firstOrNull { it.id == user2.id })
    }

    @Test
    fun removeBoardUser() {
        val user = prepare.createUser()
        val user2 = prepare.createUser(InputUserDto("Jose", "jose@example.org", "helloworld"))

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
        val boardUsers = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoardUsers.bodyString())
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
        val boardUser = Json.decodeFromString<List<SecureOutputUserDto>>(responseGetBoardUsersRemoved.bodyString())
        assertNotNull(boardUser.firstOrNull { it.id == user.id })
        assertNull(boardUser.firstOrNull { it.id == user2.id })
    }
}
