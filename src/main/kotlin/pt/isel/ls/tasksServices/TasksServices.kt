package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.CardsData
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.UsersData

class TasksServices(
    boardsRepo: BoardsData,
    usersRepo: UsersData,
    listsRepo: ListsData,
    cardsRepo: CardsData
) {
    val users = ServiceUsers(usersRepo)
    val boards = ServiceBoards(boardsRepo)
    val lists = ServiceLists(listsRepo)
    val cards = ServiceCards(cardsRepo)
}