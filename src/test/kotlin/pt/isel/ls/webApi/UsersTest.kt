package pt.isel.ls.webApi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemDataContext
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto
import pt.isel.ls.tasksServices.dtos.SecureOutputUserDto
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class UsersTest {
    private val services = TasksServices(MemDataContext, MemBoardsData, MemUsersData, MemListsData, MemCardsData)

    private val api = WebApi(services)

    private val app = routes(
        "users/{id}" bind Method.GET to api::getUser,
        "users" bind Method.POST to api::createUser
    )

    @BeforeTest
    fun clearStorage() {
        MemDataSource.clearStorage()
    }

    @Test
    fun createUser() {
        val createDto = InputUserDto("Maria", "maria@example.org", "helloworld")
        val response = app(
            Request(
                Method.POST,
                "users"
            ).body(Json.encodeToString(createDto))
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdUser = Json.decodeFromString<OutputUserDto>(response.bodyString())
        assertEquals(UUID::class, UUID.fromString(createdUser.token)::class)
    }

    @Test
    fun getUserData() {
        val createDto = InputUserDto("Maria", "maria@example.org", "helloworld")
        val response = app(
            Request(
                Method.POST,
                "users"
            ).body(Json.encodeToString(createDto))
        )

        assertEquals(Status.CREATED, response.status)
        assertEquals("application/json", response.header("content-type"))
        val createdUser = Json.decodeFromString<OutputUserDto>(response.bodyString())

        val responseGet = app(
            Request(
                Method.GET,
                "users/${createdUser.id}"
            )
        )

        assertEquals(Status.OK, responseGet.status)
        assertEquals("application/json", responseGet.header("content-type"))

        val returnedUser = Json.decodeFromString<SecureOutputUserDto>(responseGet.bodyString())

        assertEquals(createDto.name, returnedUser.name)
        assertEquals(createDto.email, returnedUser.email)
    }
}
