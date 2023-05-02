package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataContext
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.UsersData

class TasksServices(
    context: DataContext,
    boardsRepo: BoardsData,
    usersRepo: UsersData,
    listsRepo: ListsData,
    cardsRepo: CardsData
) {
    val users = ServiceUsers(context, usersRepo)
    val boards = ServiceBoards(context, boardsRepo)
    val lists = ServiceLists(context, listsRepo)
    val cards = ServiceCards(context, cardsRepo)
}
