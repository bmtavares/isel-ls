package pt.isel.ls.data.pgsql

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import java.sql.Connection

class PgSqlBoardsData(private val connection: Connection) : BoardsData {
    override fun getByName(name: String): Board? {
        val statement = connection.prepareStatement(
            "select * from Boards where name = ?;"
        )
        statement.setString(1, name)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return Board(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
            )
        }

        return null
    }

    override fun getUserBoards(user: User): List<Board> {
        val statement = connection.prepareStatement(
            "select id, name, description from Boards b join UsersBoards ub on b.id = ub.boardId where ub.userId = ?;"
        )
        statement.setInt(1, user.id!!)

        val rs = statement.executeQuery()

        val results = mutableListOf<Board>()

        while (rs.next()) {
            results += Board(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
            )
        }

        return results
    }

    override fun getById(id: Int): Board? {
        val statement = connection.prepareStatement(
            "select * from Boards where id = ?;"
        )
        statement.setInt(1, id)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return Board(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
            )
        }

        return null
    }

    override fun add(entity: Board): Board {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "insert into Board (name, description) values (?, ?);"
        )
        statement.setString(1, entity.name)
        statement.setString(2, entity.description)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to add board.")
        }

        val newBoard = getByName(entity.name)

        if (newBoard == null) {
            connection.rollback()
            throw DataException("Failed to retrieve board after adding.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState

        return newBoard
    }

    override fun delete(entity: Board) {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "delete Boards where id = ?;"
        )
        statement.setInt(1, entity.id!!)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to delete board.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState
    }

    override fun edit(entity: Board) {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "update Boards set name = ? where id = ?;" +
                    "update Boards set description = ? where id = ?;"
        )
        statement.setString(1, entity.name)
        statement.setString(3, entity.description)
        statement.setInt(2, entity.id!!)
        statement.setInt(4, entity.id)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to edit board.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState
    }

    override fun exists(entity: Board): Boolean {
        val statement = connection.prepareStatement(
            "select count(*) exists from Boards where id = ?;"
        )
        statement.setInt(1, entity.id!!)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return rs.getInt("exists") == 1
        }

        return false
    }
}
