package pt.isel.ls

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
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
import pt.isel.ls.data.mem.MemDataContext
import pt.isel.ls.data.mem.MemDataSource
import pt.isel.ls.data.mem.MemListsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.data.pgsql.PgDataContext
import pt.isel.ls.data.pgsql.PgSqlBoardsData
import pt.isel.ls.data.pgsql.PgSqlCardsData
import pt.isel.ls.data.pgsql.PgSqlListsData
import pt.isel.ls.data.pgsql.PgSqlUsersData
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.webApi.BoardsApi
import pt.isel.ls.webApi.CardsApi
import pt.isel.ls.webApi.Filters
import pt.isel.ls.webApi.ListsApi
import pt.isel.ls.webApi.UsersApi
import java.io.File

fun main() {
    val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

    // Make sure an env key for ``USE_POSTGRESQL`` exists with the value ``true`` to use the Postgresql for data
    val usePostgresql = System.getenv("USE_POSTGRESQL")?.lowercase() == "true"
    if (!usePostgresql) {
        logger.info("Using Memory for data.")
        MemDataSource.resetStorage()
    } else {
        logger.info("Using Postgres for data.")
    }

    val serverPort = System.getenv("PORT")?.toInt() ?: 9000

    val boardsRepo = if (usePostgresql) PgSqlBoardsData else MemBoardsData
    val usersRepo = if (usePostgresql) PgSqlUsersData else MemUsersData
    val listsRepo = if (usePostgresql) PgSqlListsData else MemListsData
    val cardsRepo = if (usePostgresql) PgSqlCardsData else MemCardsData
    val dataContext = if (usePostgresql) PgDataContext else MemDataContext

    val services = TasksServices(dataContext, boardsRepo, usersRepo, listsRepo, cardsRepo)

    val boardsApi = BoardsApi(services)
    val cardsApi = CardsApi(services)
    val listsApi = ListsApi(services)
    val usersApi = UsersApi(services)
    val filters = Filters(services)

    val usersRoutes = routes(
        "users/{id}" bind Method.GET to usersApi::getUser,
        "users" bind Method.POST to usersApi::createUser
    )

    val contexts = RequestContexts()
    val injectUserRoutes = ServerFilters.InitialiseRequestContext(contexts).then(filters.filterUser(contexts)).then(
        routes(
            "boards/{id}/user-list" bind Method.GET to boardsApi.getBoardUsers(contexts),
            "boards/" bind Method.GET to boardsApi.getBoards(contexts),
            "boards/" bind Method.POST to boardsApi.createBoard(contexts),
            "boards/{id}" bind Method.GET to boardsApi.getBoard(contexts)
        )
    )
    val boardRoutes = filters.authFilter.then(
        routes(
            "boards/{id}/user-list/{uid}" bind Method.PUT to boardsApi::addUsersOnBoard,
            "boards/{id}/user-list/{uid}" bind Method.DELETE to boardsApi::deleteUserFromBoard,
            "boards/{id}/lists" bind Method.GET to listsApi::getLists,
            "boards/{id}/lists" bind Method.POST to listsApi::createList,
            "boards/{id}/lists/{lid}" bind Method.PUT to listsApi::editList,
            "boards/{id}/lists/{lid}" bind Method.GET to listsApi::getList,
            "boards/{id}/lists/{lid}/cards" bind Method.GET to cardsApi::getCardsFromList,
            "boards/{id}/lists/{lid}/cards" bind Method.POST to cardsApi::createCard,
            "boards/{id}/cards/{cid}" bind Method.GET to cardsApi::getCard,
            "boards/{id}/cards/{cid}" bind Method.PUT to cardsApi::editCard,
            "boards/{id}/cards/{cid}/move" bind Method.PUT to cardsApi::alterCardListPosition,
            "boards/{id}/cards/{cid}" bind Method.DELETE to cardsApi::deleteCard,
            "boards/{id}/lists/{lid}" bind Method.DELETE to listsApi::deleteList
        )
    )

    val app = filters.logRequest(
        routes(
            "session" bind Method.POST to usersApi::loginUser,
            usersRoutes,
            boardRoutes,
            injectUserRoutes,
            "/open-api" bind Method.GET to { _: Request ->
                val fileContents = File("./open-api.json").readText()
                Response(OK).body(fileContents)
            },
            singlePageApp(ResourceLoader.Directory("static-content"))
        )
    )

    val jettyServer = app.asServer(Jetty(serverPort)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")
}
