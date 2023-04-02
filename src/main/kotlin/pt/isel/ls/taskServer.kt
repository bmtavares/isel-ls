package pt.isel.ls

import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.webApi.WebApi
import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.mem.MemBoardsData
import pt.isel.ls.data.mem.MemUsersData
import pt.isel.ls.data.pgsql.PgSqlBoardsData
import pt.isel.ls.data.pgsql.PgSqlUsersData
import java.io.File


fun main() {
    val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

    val dataRepoBoards : BoardsData = if (false){
        MemBoardsData
    }else{
        PgSqlBoardsData
    }

    val dataRepoUsers : UsersData = if (false){
        MemUsersData
    }else{
        PgSqlUsersData
    }

    val webApi = WebApi(dataRepoBoards,dataRepoUsers)

    val usersRoutes = routes(
        "users/{id}" bind Method.GET to webApi::getUser,//working
        "users" bind Method.POST to webApi::createUser,//working
    )
        val boardRoutes = webApi.authFilter.then(
            routes(
                "boards/" bind Method.GET to webApi::getBoards,//working
                "boards/{id}" bind Method.GET to webApi::getBoard,//working
                "boards/" bind Method.POST to webApi::createBoard,//working
                "boards/{id}/user-list" bind Method.GET to webApi::getBoardUsers,//working
                "boards/{id}/user-list/{uid}" bind Method.PUT to webApi::addUsersOnBoard,//working
                "boards/{id}/user-list/" bind Method.POST to webApi::alterUsersOnBoard,//is it necessary?
                "boards/{id}/user-list/{uid}" bind Method.DELETE to webApi::deleteUserFromBoard,//working
                "boards/{id}/lists" bind Method.GET to webApi::getLists,//working
                "boards/{id}/lists" bind Method.POST to webApi::createList,//working
                "boards/{id}/lists/{lid}" bind Method.PUT to webApi::editList,//working
                "boards/{id}/lists/{lid}" bind Method.GET to webApi::getList,//working
                "boards/{id}/lists/{lid}/move" bind Method.PUT to webApi::moveList,
                "boards/{id}/lists/{lid}/cards" bind Method.GET to webApi::getCardsFromList,//working
                "boards/{id}/lists/{lid}/cards" bind Method.POST to webApi::createCard,//working
                "boards/{id}/cards/{cid}" bind Method.GET to webApi::getCard,//working
                "boards/{id}/cards/{cid}" bind Method.PUT to webApi::editCard,//working but there's a problem with timestamps
                "boards/{id}/cards/{cid}/move" bind Method.GET to webApi::alterCardListPosition,//working
            )
    )
        val app = routes(
        usersRoutes,
        boardRoutes,
            "/open-api" bind Method.GET to { _: Request ->
                val fileContents = File("./open-api.json").readText()
                Response(OK).body(fileContents)
            }
    )
    val jettyServer = app.asServer(Jetty(9000)).start()
    logger.info("server started listening")

    readln()
    jettyServer.stop()

    logger.info("leaving Main")

}
