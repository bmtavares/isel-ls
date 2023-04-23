package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ListsTest {
    private val services = TasksServices(MemBoardsData, MemUsersData, MemListsData, MemCardsData)
    private val api = WebApi(services)
    private val unitApi = ListsApi(services)
    private val context = RequestContexts()
    private val prepare = ApiTestUtils(api, context)

    private val app = api.authFilter.then(
        routes(
            "boards/{id}/lists" bind Method.GET to api::getLists,
            "boards/{id}/lists" bind Method.POST to api::createList,
            "boards/{id}/lists/{lid}" bind Method.GET to api::getList,
            "boards/{id}/lists/{lid}" bind Method.DELETE to unitApi::deleteList
        )
    )

    @BeforeTest
    fun resetStorage() {
        MemDataSource.resetStorage()
    }

    @Test
    fun createList() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto = InputBoardListDto("Test Board")

        val responseCreateList = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists"
            )
                .body(Json.encodeToString(createDto))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, responseCreateList.status)
        assertEquals("application/json", responseCreateList.header("content-type"))
        val createdList = Json.decodeFromString<OutputIdDto>(responseCreateList.bodyString())
        assert(createdList.id > 0)
    }

    @Test
    fun getList() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto = InputBoardListDto("Test List")

        val responseCreateList = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists"
            )
                .body(Json.encodeToString(createDto))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, responseCreateList.status)
        assertEquals("application/json", responseCreateList.header("content-type"))
        val createdList = Json.decodeFromString<OutputIdDto>(responseCreateList.bodyString())
        assert(createdList.id > 0)

        val responseGetList = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${createdList.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetList.status)
        assertEquals("application/json", responseGetList.header("content-type"))
        val listResult = Json.decodeFromString<BoardList>(responseGetList.bodyString())

        assertEquals(createDto.name, listResult.name)
    }

    private fun simpleCreateBoard(listData: InputBoardListDto, boardId: Int, token: String) {
        app(
            Request(
                Method.POST,
                "boards/$boardId/lists"
            )
                .body(Json.encodeToString(listData))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer $token")
        ).let {
            assertEquals(Status.CREATED, it.status)
            assertEquals("application/json", it.header("content-type"))
        }
    }

    @Test
    fun getBoardLists() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        simpleCreateBoard(InputBoardListDto("Test List"), board.id, user.token)
        simpleCreateBoard(InputBoardListDto("Test List 2"), board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())
        assertEquals(2, gottenLists.size)
    }

    @Test
    fun getBoardListsWithOptionalLimit1() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto1 = InputBoardListDto("Test List")
        val createDto2 = InputBoardListDto("Test List 2")
        val createDto3 = InputBoardListDto("Test List 3")

        simpleCreateBoard(createDto1, board.id, user.token)
        simpleCreateBoard(createDto2, board.id, user.token)
        simpleCreateBoard(createDto3, board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists?limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())

        assertEquals(1, gottenLists.size)
        assertNotNull(gottenLists.firstOrNull { it.name == createDto1.name })
        assertNull(gottenLists.firstOrNull { it.name == createDto2.name })
        assertNull(gottenLists.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getBoardListsWithOptionalLimitOverSize() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto1 = InputBoardListDto("Test List")
        val createDto2 = InputBoardListDto("Test List 2")
        val createDto3 = InputBoardListDto("Test List 3")

        simpleCreateBoard(createDto1, board.id, user.token)
        simpleCreateBoard(createDto2, board.id, user.token)
        simpleCreateBoard(createDto3, board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists?limit=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())

        assertEquals(3, gottenLists.size)
        assertNotNull(gottenLists.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenLists.firstOrNull { it.name == createDto2.name })
        assertNotNull(gottenLists.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getBoardListsWithOptionalSkip1() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto1 = InputBoardListDto("Test List")
        val createDto2 = InputBoardListDto("Test List 2")
        val createDto3 = InputBoardListDto("Test List 3")

        simpleCreateBoard(createDto1, board.id, user.token)
        simpleCreateBoard(createDto2, board.id, user.token)
        simpleCreateBoard(createDto3, board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists?skip=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())

        assertEquals(2, gottenLists.size)
        assertNull(gottenLists.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenLists.firstOrNull { it.name == createDto2.name })
        assertNotNull(gottenLists.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getBoardListsWithOptionalSkipOverSize() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto1 = InputBoardListDto("Test List")
        val createDto2 = InputBoardListDto("Test List 2")
        val createDto3 = InputBoardListDto("Test List 3")

        simpleCreateBoard(createDto1, board.id, user.token)
        simpleCreateBoard(createDto2, board.id, user.token)
        simpleCreateBoard(createDto3, board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists?skip=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())

        assertEquals(0, gottenLists.size)
    }

    @Test
    fun getBoardListsWithOptionalParameters() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto1 = InputBoardListDto("Test List")
        val createDto2 = InputBoardListDto("Test List 2")
        val createDto3 = InputBoardListDto("Test List 3")

        simpleCreateBoard(createDto1, board.id, user.token)
        simpleCreateBoard(createDto2, board.id, user.token)
        simpleCreateBoard(createDto3, board.id, user.token)

        val responseGetLists = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists?skip=1&limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetLists.status)
        assertEquals("application/json", responseGetLists.header("content-type"))
        val gottenLists = Json.decodeFromString<List<BoardList>>(responseGetLists.bodyString())

        assertEquals(1, gottenLists.size)
        assertNull(gottenLists.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenLists.firstOrNull { it.name == createDto2.name })
        assertNull(gottenLists.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun deleteBoardList() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto = InputBoardListDto("Test List")

        val responseCreateList = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists"
            )
                .body(Json.encodeToString(createDto))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, responseCreateList.status)
        assertEquals("application/json", responseCreateList.header("content-type"))
        val createdList = Json.decodeFromString<OutputIdDto>(responseCreateList.bodyString())

        app(
            Request(
                Method.DELETE,
                "boards/${board.id}/lists/${createdList.id}"
            )
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.OK, it.status)
        }
    }

    @Test
    fun deleteNonExistentBoardList() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)

        val createDto = InputBoardListDto("Test List")

        app(
            Request(
                Method.POST,
                "boards/${board.id}/lists"
            )
                .body(Json.encodeToString(createDto))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.CREATED, it.status)
            assertEquals("application/json", it.header("content-type"))
        }

        app(
            Request(
                Method.DELETE,
                "boards/${board.id}/lists/-1"
            )
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.BAD_REQUEST, it.status)
            assertEquals("application/json", it.header("content-type"))
            assertEquals("List not found.", Json.decodeFromString(it.bodyString()))
        }
    }
}
