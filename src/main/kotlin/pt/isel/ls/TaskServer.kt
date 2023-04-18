package pt.isel.ls

import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemCardsData
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.data.pgsql.PgSqlBoardsData
import pt.isel.ls.data.pgsql.PgSqlCardsData
import pt.isel.ls.data.pgsql.PgSqlListsData
import pt.isel.ls.data.pgsql.PgSqlUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.webApi.WebApi
import java.io.File

fun main() {
    val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

    // Make sure an env key for ``USE_POSTGRESQL`` exists with the value ``true`` to use the Postgresql for data
    val usePostgresql = System.getenv("USE_POSTGRESQL").lowercase() == "true"

    val boardsRepo = if (usePostgresql) PgSqlBoardsData else MemBoardsData
    val usersRepo = if (usePostgresql) PgSqlUsersData else MemUsersData
    val listsRepo = if (usePostgresql) PgSqlListsData else MemListsData
    val cardsRepo = if (usePostgresql) PgSqlCardsData else MemCardsData

    val services = TasksServices(boardsRepo, usersRepo, listsRepo, cardsRepo)

    val webApi = WebApi(services)

    val usersRoutes = routes(
        "users/{id}" bind Method.GET to webApi::getUser, // working
        "users" bind Method.POST to webApi::createUser // working
    )
    val contexts = RequestContexts()
    val injectUserRoutes = ServerFilters.InitialiseRequestContext(contexts).then(webApi.filterUser(contexts)).then(
        routes(
            "boards/{id}/user-list" bind Method.GET to webApi.getBoardUsers(contexts),
            "boards/" bind Method.GET to webApi.getBoards(contexts),
            "boards/" bind Method.POST to webApi.createBoard(contexts),
            "boards/{id}" bind Method.GET to webApi.getBoard(contexts), // working
        )
    )
    val boardRoutes = webApi.authFilter.then(
        routes(
            //"boards/" bind Method.GET to webApi::getBoards, // working
            //"boards/" bind Method.POST to webApi::createBoard, // working
           // "boards/{id}/user-list" bind Method.GET to webApi::getBoardUsers, // working
            "boards/{id}/user-list/{uid}" bind Method.PUT to webApi::addUsersOnBoard, // working// todo add user to body - url boards/{id}
            "boards/{id}/user-list/" bind Method.POST to webApi::alterUsersOnBoard, // is it necessary?
            "boards/{id}/user-list/{uid}" bind Method.DELETE to webApi::deleteUserFromBoard, // working todo not needed
            "boards/{id}/lists" bind Method.GET to webApi::getLists, // working
            "boards/{id}/lists" bind Method.POST to webApi::createList, // working
            "boards/{id}/lists/{lid}" bind Method.PUT to webApi::editList, // working
            "boards/{id}/lists/{lid}" bind Method.GET to webApi::getList, // working
            "boards/{id}/lists/{lid}/move" bind Method.PUT to webApi::moveList,
            "boards/{id}/lists/{lid}/cards" bind Method.GET to webApi::getCardsFromList, // working
            "boards/{id}/lists/{lid}/cards" bind Method.POST to webApi::createCard, // working
            "boards/{id}/cards/{cid}" bind Method.GET to webApi::getCard, // working
            "boards/{id}/cards/{cid}" bind Method.PUT to webApi::editCard, // working but there's a problem with timestamps
            "boards/{id}/cards/{cid}/move" bind Method.GET to webApi::alterCardListPosition // working  todo channge to put add new position on the body
        )
    )

    val app = routes(
        usersRoutes,
        boardRoutes,
        injectUserRoutes,
        "/open-api" bind Method.GET to { _: Request ->
            val fileContents = File("./open-api.json").readText()
            Response(OK).body(fileContents)
        },
        singlePageApp(ResourceLoader.Directory("static-content"))
    )

    val jettyServer = app.asServer(Jetty(9000)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")
}
