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
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import pt.isel.ls.tasksServices.dtos.OutputIdDto
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CardsTest {
    private val services = TasksServices(MemBoardsData, MemUsersData, MemListsData, MemCardsData)
    private val api = WebApi(services)
    private val unitApi = CardsApi(services)
    private val context = RequestContexts()
    private val prepare = ApiTestUtils(api, context)

    private val app = api.authFilter.then(
        routes(
            "boards/{id}/lists/{lid}/cards" bind Method.POST to api::createCard,
            "boards/{id}/lists/{lid}/cards" bind Method.GET to api::getCardsFromList,
            "boards/{id}/cards/{cid}" bind Method.GET to api::getCard,
            "boards/{id}/cards/{cid}/move" bind Method.GET to api::alterCardListPosition,
            "boards/{id}/cards/{cid}" bind Method.DELETE to unitApi::deleteCard
        )
    )

    @BeforeTest
    fun resetStorage() {
        MemDataSource.resetStorage()
    }

    @Test
    fun createCard() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val response = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdCard = Json.decodeFromString<OutputIdDto>(response.bodyString())
        assert(createdCard.id > 0)
    }

    @Test
    fun getCard() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val response = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdCard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        val responseGet = app(
            Request(
                Method.GET,
                "boards/${board.id}/cards/${createdCard.id}"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGet.status)
        assertEquals("application/json", responseGet.header("content-type"))
        val gottenCard = Json.decodeFromString<Card>(responseGet.bodyString())

        assertEquals(createDto.name, gottenCard.name)
        assertEquals(createDto.description, gottenCard.description)
        assertEquals(createDto.dueDate, gottenCard.dueDate)
    }

    private fun simpleCreateCard(createDto: InputCardDto, boardId: Int, listId: Int, token: String) {
        app(
            Request(
                Method.POST,
                "boards/$boardId/lists/$listId/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer $token")
        ).let {
            assertEquals(Status.CREATED, it.status)
            assertEquals("application/json", it.header("content-type"))
        }
    }

    @Test
    fun getListCards() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val response = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))

        val responseGetListCards = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, responseGetListCards.status)
        assertEquals("application/json", responseGetListCards.header("content-type"))

        val gottenListCards = Json.decodeFromString<List<Card>>(responseGetListCards.bodyString())

        assertEquals(1, gottenListCards.size)
    }

    @Test
    fun getListCardsWithOptionalLimit1() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto1 = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val createDto2 = createDto1.copy(name = "Test Card 2")
        val createDto3 = createDto1.copy(name = "Test Card 3")

        simpleCreateCard(createDto1, board.id, boardList.id, user.token)
        simpleCreateCard(createDto2, board.id, boardList.id, user.token)
        simpleCreateCard(createDto3, board.id, boardList.id, user.token)

        val response = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards?limit=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        val gottenListCards = Json.decodeFromString<List<Card>>(response.bodyString())

        assertEquals(1, gottenListCards.size)
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto1.name })
        assertNull(gottenListCards.firstOrNull { it.name == createDto2.name })
        assertNull(gottenListCards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getListCardsWithOptionalLimitOverSize() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto1 = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val createDto2 = createDto1.copy(name = "Test Card 2")
        val createDto3 = createDto1.copy(name = "Test Card 3")

        simpleCreateCard(createDto1, board.id, boardList.id, user.token)
        simpleCreateCard(createDto2, board.id, boardList.id, user.token)
        simpleCreateCard(createDto3, board.id, boardList.id, user.token)

        val response = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards?limit=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        val gottenListCards = Json.decodeFromString<List<Card>>(response.bodyString())

        assertEquals(3, gottenListCards.size)
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto2.name })
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getListCardsWithOptionalSkip1() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto1 = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val createDto2 = createDto1.copy(name = "Test Card 2")
        val createDto3 = createDto1.copy(name = "Test Card 3")

        simpleCreateCard(createDto1, board.id, boardList.id, user.token)
        simpleCreateCard(createDto2, board.id, boardList.id, user.token)
        simpleCreateCard(createDto3, board.id, boardList.id, user.token)

        val response = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards?skip=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        val gottenListCards = Json.decodeFromString<List<Card>>(response.bodyString())

        assertEquals(2, gottenListCards.size)
        assertNull(gottenListCards.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto2.name })
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun getListCardsWithOptionalSkipOverSize() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto1 = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val createDto2 = createDto1.copy(name = "Test Card 2")
        val createDto3 = createDto1.copy(name = "Test Card 3")

        simpleCreateCard(createDto1, board.id, boardList.id, user.token)
        simpleCreateCard(createDto2, board.id, boardList.id, user.token)
        simpleCreateCard(createDto3, board.id, boardList.id, user.token)

        val response = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards?skip=5"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        val gottenListCards = Json.decodeFromString<List<Card>>(response.bodyString())

        assertEquals(0, gottenListCards.size)
    }

    @Test
    fun getListCardsWithOptionalParameters() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto1 = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val createDto2 = createDto1.copy(name = "Test Card 2")
        val createDto3 = createDto1.copy(name = "Test Card 3")

        simpleCreateCard(createDto1, board.id, boardList.id, user.token)
        simpleCreateCard(createDto2, board.id, boardList.id, user.token)
        simpleCreateCard(createDto3, board.id, boardList.id, user.token)

        val response = app(
            Request(
                Method.GET,
                "boards/${board.id}/lists/${boardList.id}/cards?limit=1&skip=1"
            )
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("content-type"))
        val gottenListCards = Json.decodeFromString<List<Card>>(response.bodyString())

        assertEquals(1, gottenListCards.size)
        assertNull(gottenListCards.firstOrNull { it.name == createDto1.name })
        assertNotNull(gottenListCards.firstOrNull { it.name == createDto2.name })
        assertNull(gottenListCards.firstOrNull { it.name == createDto3.name })
    }

    @Test
    fun moveCard() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val originBoardList = prepare.createList(user.token, board.id)
        val destinationBoardList = prepare.createList(user.token, board.id, InputBoardListDto("Cooler List"))

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )
        val responseCreate = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${originBoardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, responseCreate.status)
        assertEquals("application/json", responseCreate.header("content-type"))

        val createdCard = Json.decodeFromString<OutputIdDto>(responseCreate.bodyString())

        val request = InputMoveCardDto(destinationBoardList.id, 0)

        app(
            Request(
                Method.GET,
                "boards/${board.id}/cards/${createdCard.id}/move"
            )
                .body(Json.encodeToString(request))
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.OK, it.status)
        }

        app(
            Request(
                Method.GET,
                "boards/${board.id}/cards/${createdCard.id}"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.OK, it.status)
            assertEquals("application/json", it.header("content-type"))

            val gottenCard = Json.decodeFromString<Card>(it.bodyString())

            assertEquals(destinationBoardList.id, gottenCard.listId)
        }
    }

    @Test
    fun deleteCard() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )

        val response = app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        )
        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdCard = Json.decodeFromString<OutputIdDto>(response.bodyString())

        app(
            Request(
                Method.DELETE,
                "boards/${board.id}/cards/${createdCard.id}"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.OK, it.status)
        }
    }

    @Test
    fun deleteNonExistentCard() {
        val user = prepare.createUser()
        val board = prepare.createBoard(user.token)
        val boardList = prepare.createList(user.token, board.id)

        val createDto = InputCardDto(
            "Test Card",
            "Card used for integration testing"
        )

        app(
            Request(
                Method.POST,
                "boards/${board.id}/lists/${boardList.id}/cards"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.CREATED, it.status)
            assertEquals("application/json", it.header("content-type"))
        }

        app(
            Request(
                Method.DELETE,
                "boards/${board.id}/cards/-1"
            )
                .body(Json.encodeToString(createDto))
                .header("Authorization", "Bearer ${user.token}")
        ).let {
            assertEquals(Status.BAD_REQUEST, it.status)
            assertEquals("application/json", it.header("content-type"))
            assertEquals("Card not found.", Json.decodeFromString(it.bodyString()))
        }
    }
}
