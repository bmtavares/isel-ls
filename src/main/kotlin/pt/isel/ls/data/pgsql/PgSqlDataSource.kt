package pt.isel.ls.data.pgsql;

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataSource
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.UsersData
import java.sql.Connection

class PgSqlDataSource(private val connection : Connection) : DataSource {
    override val users: UsersData
        get() = PgSqlUsersData(connection)
    override val boards: BoardsData
        get() = PgSqlBoardsData(connection)
    override val lists: ListsData
        get() = PgSqlListsData(connection)
    override val cards: CardsData
        get() = TODO("Not yet implemented")
}
