package pt.isel.ls.data.pgsql

import pt.isel.ls.data.DataException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

object PgSqlListsData : ListsData {
    override fun getListsByBoard(boardId: Int, limit: Int, skip: Int): List<BoardList> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select l.id , l.name , l.boardId  from Lists l join Boards b on l.boardId = b.id where b.id = ? offset ? limit ?;"
            )
            statement.setInt(1, boardId)
            statement.setInt(2, skip)
            statement.setInt(3, limit)

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

    override fun getById(id: Int): BoardList {
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

            throw Exception("awdwa") // TODO
        }
    }

    override fun add(newBoardList: InputBoardListDto, boardId: Int): BoardList {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Lists (name, boardId) values (?, ?) returning id, name, boardId;"
            )
            statement.setString(1, newBoardList.name)
            statement.setInt(2, boardId)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val bId = rs.getInt("boardId")

                it.commit()

                return BoardList(
                    id,
                    name,
                    bId
                )
            }

            it.rollback()
            throw DataException("Failed to add list.")
        }
    }

    override fun delete(id: Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete from Lists where id = ?;"
            )
            statement.setInt(1, id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete list.")
            }

            it.commit()
        }
    }

    override fun edit(editName: String, listId: Int, boardId: Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Lists set name = ? where id = ? and boardid = ?;"
            )
            statement.setString(1, editName)
            statement.setInt(2, listId)
            statement.setInt(3, boardId)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit list.")
            }

            it.commit()
        }
    }

    override fun exists(id: Int): Boolean {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select count(*) exists from Boards where id = ?;"
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
