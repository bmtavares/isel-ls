package pt.isel.ls.http

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.tasksServices.ServiceUsers

private val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

@Serializable
data class User(val name: String, val number: Int)


val users = mutableListOf(
    User("ff", 10),
    User("ll", 20),
    User("dd", 30)
)

fun getUsers(request: Request): Response {
    logRequest(request)
    val limit = request.query("limit")?.toInt() ?: 4
    return Response(OK)
        .header("content-type", "application/json")
        .body(Json.encodeToString(users.take(limit)))
}

fun getUser(request: Request): Response {
    logRequest(request)
    val userNumber = request.path("number")?.toInt()
    return Response(OK)
        .header("content-type", "application/json")
        .body(Json.encodeToString(users.find { it.number == userNumber }))
}

fun postUser(request: Request): Response {
    logRequest(request)
    val user = Json.decodeFromString<User>(request.bodyString())
    users.add(user)
    return Response(CREATED)
        .header("content-type", "application/json")
        .body(Json.encodeToString(user))
}

fun postCreateUser(request: Request): Response {
    val user = Json.decodeFromString<User>(request.bodyString())


    return Response(CREATED)
        .header("content-type", "application/json")
        .body(Json.encodeToString(user))
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
    val usersRoutes = routes(
        "users" bind GET to ::getUsers,
        "users/{number}" bind GET to ::getUser,
        "users" bind POST to ::postUser,
        "user" bind POST to ::postUser
    )

    val app = routes(
        usersRoutes,
        "date" bind GET to ::getDate
    )

    val jettyServer = app.asServer(Jetty(9000)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")
}
