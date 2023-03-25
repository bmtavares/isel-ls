package pt.isel.ls.data.pgsql

import pt.isel.ls.data.DataException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import java.sql.Connection

class PgSqlListsData : ListsData {
    override fun getListsByBoard(board: Board): List<BoardList> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select l.id id, l.name name, l.boardId boardId from Lists l join Boards b on l.boardId = b.id where b.id = ?;"
            )
            statement.setInt(1, board.id!!)

            val rs = statement.executeQuery()

            val results = mutableListOf<BoardList>()

            while (rs.next()) {
                results += BoardList(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("boardId")
                )
            }

            return results
        }
    }

    override fun getById(id: Int): BoardList? {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select * from Lists where id = ?;"
            )
            statement.setInt(1, id)

            val rs = statement.executeQuery()
            while (rs.next()) {
                return BoardList(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("boardId")
                )
            }

            return null
        }
    }

    override fun add(entity: BoardList): BoardList {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Lists (name, boardId) values (?, ?) returning id, name, boardId;"
            )
            statement.setString(1, entity.name)
            statement.setInt(2, entity.boardId)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val boardId = rs.getInt("email")

                it.commit()

                return BoardList(
                    id,
                    name,
                    boardId
                )
            }

            it.rollback()
            throw DataException("Failed to add list.")
        }
    }

    override fun delete(entity: BoardList) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete Lists where id = ?;"
            )
            statement.setInt(1, entity.id!!)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete list.")
            }

            it.commit()
        }
    }

    override fun edit(entity: BoardList) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Lists set name = ? where id = ?;"
            )
            statement.setString(1, entity.name)
            statement.setInt(2, entity.id!!)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit list.")
            }

            it.commit()
        }
    }

    override fun exists(entity: BoardList): Boolean {
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
}