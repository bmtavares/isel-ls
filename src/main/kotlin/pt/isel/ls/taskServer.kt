package pt.isel.ls

import org.eclipse.jetty.server.Authentication.User
import org.http4k.core.Method
import org.http4k.core.then
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
        "users/{id}" bind Method.GET to webApi::getUser,
        "users" bind Method.POST to webApi::createUser,
    )
        val boardRoutes = webApi.authFilter.then(
            routes(
                "boards/{id}" bind Method.GET to webApi::getBoard,
                "boards/" bind Method.POST to webApi::createBoard,
                "boards/{id}/user-list" bind Method.GET to webApi::getBoardUsers,
                "boards/{id}/user-list/{uid}" bind Method.PUT to webApi::alterUsersOnBoard,
                "boards/{id}/user-list/{uid}" bind Method.DELETE to webApi::deleteUsersFromBoard,
                "boards/{id}/lists" bind Method.GET to webApi::getLists,
                "boards/{id}/lists" bind Method.POST to webApi::createList,
                "boards/{id}/lists/{lid}" bind Method.PUT to webApi::editList,
                "boards/{id}/lists/{lid}" bind Method.GET to webApi::getList,
                "boards/{id}/lists/{lid}/move" bind Method.PUT to webApi::moveList,
                "boards/{id}/lists/{lid}/cards" bind Method.GET to webApi::getCardsFromList,
                "boards/{id}/lists/{lid}/cards" bind Method.POST to webApi::createCard,
                "boards/{id}/cards/{cid}" bind Method.GET to webApi::getAllCards,
                "boards/{id}/cards/{cid}/move" bind Method.GET to webApi::alterListPosition,
            )
    )
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
