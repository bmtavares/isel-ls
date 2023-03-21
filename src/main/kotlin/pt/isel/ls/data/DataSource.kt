package pt.isel.ls.data

interface DataSource {
    val users: UsersData
    val boards: BoardsData
    val lists: ListsData
    val cards: CardsData
}
