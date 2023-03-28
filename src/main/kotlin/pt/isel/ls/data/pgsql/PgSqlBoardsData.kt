package pt.isel.ls.data.pgsql

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import java.sql.Connection
import kotlin.math.E

object PgSqlBoardsData : BoardsData {
    override fun getByName(name: String): Board? {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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

            throw Exception("awdwa") // TODO
        }
    }

    override fun getUserBoards(user: User): List<Board> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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
    }

    override fun getById(id: Int): Board{
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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

            throw Exception("awda") // TODO
        }
    }

    override fun add(entity: Board): Board {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Board (name, description) values (?, ?);"
            )
            statement.setString(1, entity.name)
            statement.setString(2, entity.description)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to add board.")
            }

            val newBoard = getByName(entity.name)

            if (newBoard == null) {
                it.rollback()
                throw DataException("Failed to retrieve board after adding.")
            }

            it.commit()

            return newBoard
        }

    }

    override fun delete(entity: Board) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete Boards where id = ?;"
            )
            statement.setInt(1, entity.id!!)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete board.")
            }

            it.commit()
        }
    }

    override fun edit(entity: Board) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Boards set name = ? where id = ?;" +
                        "update Boards set description = ? where id = ?;"
            )
            statement.setString(1, entity.name)
            statement.setString(3, entity.description)
            statement.setInt(2, entity.id!!)
            statement.setInt(4, entity.id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit board.")
            }

            it.commit()
        }
    }

    override fun exists(entity: Board): Boolean {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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

    operator fun invoke(): BoardsData {
        return this
    }
}
