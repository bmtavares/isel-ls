package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataSource
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard
import pt.isel.ls.data.entities.UserToken

object MemDataSource : DataSource {
    private val usersList = mutableListOf<User>()
    private val userTokensList = mutableListOf<UserToken>()
    private val boardsList = mutableListOf<Board>()
    private val usersBoardsList = mutableListOf<UserBoard>()
    private val listsList = mutableListOf<BoardList>()
    private val cardsList = mutableListOf<Card>()

    override val users: UsersData
        get() = MemUsersData(usersList, userTokensList)
    override val boards: BoardsData
        get() = MemBoardsData(boardsList, usersBoardsList)
    override val lists: ListsData
        get() = MemListsData(listsList)
    override val cards: CardsData
        get() = MemCardsData(cardsList)

    fun clearStorage() {
        usersList.clear()
        userTokensList.clear()
        boardsList.clear()
        usersBoardsList.clear()
        listsList.clear()
        cardsList.clear()
        (users as MemUsersData).clearStorage()
        (boards as MemBoardsData).clearStorage()
        (lists as MemListsData).clearStorage()
        (cards as MemCardsData).clearStorage()
    }
}
