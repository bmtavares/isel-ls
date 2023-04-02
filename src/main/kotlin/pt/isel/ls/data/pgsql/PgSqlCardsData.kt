package pt.isel.ls.data.pgsql

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import java.sql.Types

object PgSqlCardsData : CardsData {
    override fun getByList(boardId: Int, listId: Int): List<Card> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select * from Cards where listId = ? and boardid = ?;"
            )
            statement.setInt(1, listId)
            statement.setInt(2, boardId)

            val rs = statement.executeQuery()

            val results = mutableListOf<Card>()

            while (rs.next()) {
                results += Card(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("dueDate"),
                    rs.getInt("listId"),
                    rs.getInt("boardId"),
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

    override fun getById(id: Int): Card {
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
                    rs.getInt("listId"),
                    rs.getInt("boardId")
                )
            }

            throw Exception("awdwa") // TODO
        }
    }

    override fun add(newCard: InputCardDto,boardId:Int,listId:Int?): Card {
        PgDataContext.getConnection().use {
            val previousCommitState = it.autoCommit
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Cards (name, description, dueDate, listId, boardId) values (?, ?, ?, ?, ?) returning id, name, description, dueDate, listId, boardId;"
            )
            statement.setString(1, newCard.name)
            statement.setString(2, newCard.description)
            if (newCard.dueDate == null) {
                statement.setNull(3, Types.TIMESTAMP)
            } else {
                statement.setTimestamp(3, newCard.dueDate)
            }
            if (listId != null)
                statement.setInt(4, listId)
            else
                statement.setNull(4,Types.INTEGER)

            statement.setInt(5, boardId)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val description = rs.getString("description")
                val dueDate = rs.getTimestamp("dueDate")
                val lId = if (rs.wasNull()) null else rs.getInt("listId")
                val bId = rs.getInt("boardId")

                it.commit()
                it.autoCommit = previousCommitState

                return Card(
                    id,
                    name,
                    description,
                    dueDate,
                    lId,
                    bId
                )
            }

            it.rollback()
            throw DataException("Failed to add card.")
        }
    }

    override fun delete(id: Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete from Cards where id = ?;"
            )
            statement.setInt(1, id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete card.")
            }

            it.commit()
        }
    }

    override fun edit(editCardDto: EditCardDto, boardId: Int, cardId:Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Cards set name = ? where id = ?;" +
                        "update Cards set description = ? where id = ?;" +
                        "update Cards set dueDate = ? where id = ?;" +
                        "update Cards set listId = ? where id = ?;"
            )
            statement.setString(1, editCardDto.name)
            statement.setString(3, editCardDto.description)
            if (editCardDto.dueDate == null) {
                statement.setNull(5, Types.TIMESTAMP)
            } else {
                statement.setTimestamp(5, editCardDto.dueDate)
            }

            if (editCardDto.listId == null) {
                statement.setNull(7, Types.INTEGER)
            } else {
                statement.setInt(7, editCardDto.listId)
            }
            statement.setInt(2, cardId)
            statement.setInt(4, cardId)
            statement.setInt(6, cardId)
            statement.setInt(8, cardId)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit card.")
            }

            it.commit()
        }
    }

    override fun exists(id: Int): Boolean {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select count(*) exists from Cards where id = ?;"
            )
            statement.setInt(1, id)

            val rs = statement.executeQuery()
            while (rs.next()) {
                return rs.getInt("exists") == 1
            }

            return false
        }
    }
}
