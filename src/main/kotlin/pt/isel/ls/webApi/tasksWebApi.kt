package pt.isel.ls.http

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ClientFilters
import org.http4k.metrics.MetricsDefaults.Companion.server
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.security.CredentialsProvider
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.server.User
import pt.isel.ls.tasksServices.ServiceUsers
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.UserResponses
import java.util.*

private val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")
private val services = TasksServices()


class WebApi{
    val usersRoutes = routes(
        "users" bind GET to ::getUsers,
        "users/{id}" bind GET to ::getUser,
        "users" bind POST to ::postUser,
        "user" bind POST to ::postUser
    )
    val boardRoutes = routes(
        "boards/{id}" bind GET to ::getBoard,
        "boards/{id}/user-list/add" bind Method.PUT to ::getBoard,
        "boards/{id}/user-list/del" bind Method.DELETE to ::getBoard,
        "boards/{id}/lists" bind GET to ::getBoard,
        "boards/{id}/lists" bind POST to ::getBoard,
        "lists/{id}" bind POST to ::getBoard,
        "boards/{id}/lists/{lid}" bind GET to ::getBoard,
        "boards/{id}/lists/{lid}/cards" bind GET to ::getBoard,
        "boards/{id}/lists/{lid}/cards" bind POST to ::getBoard,
        "boards/{id}/cards/{cid}" bind GET to ::getBoard,
        "boards/{id}/cards/{cid}/move" bind GET to ::getBoard,//to check
    )

}


val users = mutableListOf(
    User(1, "aa","aa@gmail.com"),
    User(1,"ll", "ll@gmail.com"),
    User(1,"dd", "dd@gmail.com")
)

fun getUsers(request: Request): Response {
    logRequest(request)
    val limit = request.query("limit")?.toInt() ?: 4
    return Response(OK)
        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
        .body(Json.encodeToString(users.take(limit)))
}

fun getUser(request: Request): Response {
    logRequest(request)
    val email = request.path("email")
    return Response(OK)
        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
        .body(Json.encodeToString(users.find { it.email == email }))
}

fun postUser(request: Request): Response {
    logRequest(request)
    val user = Json.decodeFromString<User>(request.bodyString())
    users.add(user)
    return Response(CREATED)
        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
        .body(Json.encodeToString(user))
}

fun postCreateUser(request: Request): Response {
    val user = Json.decodeFromString<User>(request.bodyString())
    val result = services.users.createUser(user)
    val resp = if(result == UserResponses.Created){  CREATED  }else{ BAD_REQUEST  }
    return Response(resp)
        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
        .body(Json.encodeToString(user))
}

fun getBoard(request: Request):Response {
    logRequest(request)
    val boardId = request.path("id")
    return Response(OK)
        .header(HeaderTypes.ContentType.field, ContentType.APPLICATION_JSON.value)
        .body(Json.encodeToString(users.find { it.email == boardId }))

}


/*
fun getDate(request: Request): Response {
    return Response(OK)
        .header("content-type", "text/plain")
        .body(Clock.System.now().toString())
}*/

/*
fun logRequest(request: Request) {
    logger.info(
        "incoming request: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept")
    )
}
*/
fun main() {

    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                val uuidToken =  token
                if (verifyToken(uuidToken)) {
                    next(request)
                } else {
                    Response(Status.UNAUTHORIZED).body("Invalid token")
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }

    val usersRoutes = routes(
        "users" bind GET to ::getUsers,
        "users/{id}" bind GET to ::getUser,
        "users" bind POST to ::postUser,
    )
    val boardRoutes = authFilter.then(routes(
        "boards/{id}" bind GET to ::getBoard,
        "boards/{id}/user-list/add" bind Method.PUT to ::getBoard,
        "boards/{id}/user-list/del" bind Method.DELETE to ::getBoard,
        "boards/{id}/lists" bind GET to ::getBoard,
        "boards/{id}/lists" bind POST to ::getBoard,
        "lists/{id}" bind POST to ::getBoard,
        "boards/{id}/lists/{lid}" bind GET to ::getBoard,
        "boards/{id}/lists/{lid}/cards" bind GET to ::getBoard,
        "boards/{id}/lists/{lid}/cards" bind POST to ::getBoard,
        "boards/{id}/cards/{cid}" bind GET to ::getBoard,
        "boards/{id}/cards/{cid}/move" bind GET to ::getBoard,//to check
    ))

    val app = routes(
        usersRoutes,
        boardRoutes
    )

    val jettyServer = app.asServer(Jetty(9000)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")

}
fun verifyToken(token:String):Boolean {
    val userResponse = services.users.getUserByToken(token)
    return (userResponse?.first != null)
}