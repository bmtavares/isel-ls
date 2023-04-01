package pt.isel.ls.data.mem

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard
import pt.isel.ls.data.entities.UserToken

object MemDataSource {
    val users = mutableListOf<User>()
    val usersTokens = mutableListOf<UserToken>()
    val boards = mutableListOf<Board>()
    val usersBoards = mutableListOf<UserBoard>()
    val lists = mutableListOf<BoardList>()
    val cards = mutableListOf<Card>()

    fun clearStorage() {
        users.clear()
        usersTokens.clear()
        boards.clear()
        usersBoards.clear()
        lists.clear()
        cards.clear()
    }
}
