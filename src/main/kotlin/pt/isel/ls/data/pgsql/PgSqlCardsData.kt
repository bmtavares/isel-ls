package pt.isel.ls.data.pgsql

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import java.sql.Connection
import java.sql.Types

class PgSqlCardsData : CardsData {
    override fun getByList(list: BoardList): List<Card> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select * from Cards where listId = ?;"
            )
            statement.setInt(1, list.id!!)

            val rs = statement.executeQuery()

            val results = mutableListOf<Card>()

            while (rs.next()) {
                results += Card(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("dueDate"),
                    if (rs.wasNull()) null else rs.getInt("listId"),
                    rs.getInt("boardId")
                )
            }

            return results
        }
    }

    override fun getByBoard(board: Board): List<Card> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select * from Cards where boardId = ?;"
            )
            statement.setInt(1, board.id!!)

            val rs = statement.executeQuery()

            val results = mutableListOf<Card>()

            while (rs.next()) {
                results += Card(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("dueDate"),
                    if (rs.wasNull()) null else rs.getInt("listId"),
                    rs.getInt("boardId")
                )
            }

            return results
        }
    }

    override fun getById(id: Int): Card? {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select * from Cards where id = ?;"
            )
            statement.setInt(1, id)

            val rs = statement.executeQuery()
            while (rs.next()) {
                return Card(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("dueDate"),
                    if (rs.wasNull()) null else rs.getInt("listId"),
                    rs.getInt("boardId")
                )
            }

            return null
        }
    }

    override fun add(entity: Card): Card {
        PgDataContext.getConnection().use {
            val previousCommitState = it.autoCommit
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Cards (name, description, dueDate, listId, boardId) values (?, ?, ?, ?, ?) returning id, name, description, dueDate, listId, boardId;"
            )
            statement.setString(1, entity.name)
            statement.setInt(2, entity.boardId)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val description = rs.getString("description")
                val dueDate = rs.getTimestamp("dueDate")
                val listId = if (rs.wasNull()) null else rs.getInt("listId")
                val boardId = rs.getInt("boardId")

                it.commit()
                it.autoCommit = previousCommitState

                return Card(
                    id,
                    name,
                    description,
                    dueDate,
                    listId,
                    boardId
                )
            }

            it.rollback()
            throw DataException("Failed to add card.")
        }
    }

    override fun delete(entity: Card) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete Cards where id = ?;"
            )
            statement.setInt(1, entity.id!!)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete card.")
            }

            it.commit()
        }
    }

    override fun edit(entity: Card) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Cards set name = ? where id = ?;" +
                        "update Cards set description = ? where id = ?;" +
                        "update Cards set dueDate = ? where id = ?;" +
                        "update Cards set listId = ? where id = ?;"
            )
            statement.setString(1, entity.name)
            statement.setString(3, entity.name)
            statement.setTimestamp(5, entity.dueDate)
            if (entity.listId == null) {
                statement.setNull(7, Types.INTEGER)
            } else {
                statement.setInt(7, entity.listId!!)
            }
            statement.setInt(2, entity.id!!)
            statement.setInt(4, entity.id)
            statement.setInt(6, entity.id)
            statement.setInt(8, entity.id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit card.")
            }

            it.commit()
        }
    }

    override fun exists(entity: Card): Boolean {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select count(*) exists from Cards where id = ?;"
            )
            statement.setInt(1, entity.id!!)

            val rs = statement.executeQuery()
            while (rs.next()) {
                return rs.getInt("exists") == 1
            }

            return false
        }
    }
}
