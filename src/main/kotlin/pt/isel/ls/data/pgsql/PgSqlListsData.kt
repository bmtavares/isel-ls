package pt.isel.ls.data.pgsql

import org.postgresql.util.PSQLState
import pt.isel.ls.TaskAppException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection
import java.sql.SQLException

object PgSqlListsData : ListsData {
    override fun getListsByBoard(boardId: Int, limit: Int, skip: Int, connection: Connection?): List<BoardList> {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
            "select l.id , l.name , l.boardId, l.ncards  from Lists l join Boards b on l.boardId = b.id where b.id = ? offset ? limit ?;"
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
                rs.getInt("boardId"),
                rs.getInt("ncards")
            )
        }

        return results
    }

    override fun getById(id: Int, connection: Connection?): BoardList {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
            "select * from Lists where id = ?;"
        )
        statement.setInt(1, id)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return BoardList(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("boardId"),
                rs.getInt("ncards")
            )
        }

        throw TaskAppException(ErrorCodes.LIST_READ_FAIL)
    }

    override fun add(newBoardList: InputBoardListDto, boardId: Int, connection: Connection?): BoardList {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
            "insert into Lists (name, boardId,ncards) values (?, ?,?) returning id, name, boardId, ncards;"
        )
        statement.setString(1, newBoardList.name)
        statement.setInt(2, boardId)
        statement.setInt(3, 0)

        val rs = try {
            statement.executeQuery()
        } catch (ex: SQLException) {
            throw if (ex.sqlState == PSQLState.FOREIGN_KEY_VIOLATION.state) {
                TaskAppException(ErrorCodes.LIST_CREATE_FOREIGN_KEY_FAIL)
            } else {
                ex
            }
        }

        while (rs.next()) {
            val id = rs.getInt("id")
            val name = rs.getString("name")
            val bId = rs.getInt("boardId")
            val ncards = rs.getInt("ncards")

            return BoardList(
                id,
                name,
                bId,
                ncards
            )
        }

        throw TaskAppException(ErrorCodes.LIST_CREATE_FAIL)
    }

    override fun delete(id: Int, connection: Connection?) {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
            "delete from Lists where id = ?;"
        )
        statement.setInt(1, id)

        val count = statement.executeUpdate()

        if (count == 0) throw TaskAppException(ErrorCodes.LIST_DELETE_FAIL)
    }

    override fun edit(editName: String, listId: Int, boardId: Int, ncards: Int, connection: Connection?) {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
            "update Lists set name = ?,ncards = ? where id = ? and boardid = ?;"
        )
        statement.setString(1, editName)
        statement.setInt(2, ncards)
        statement.setInt(3, listId)
        statement.setInt(4, boardId)

        val count = statement.executeUpdate()

        if (count == 0) throw TaskAppException(ErrorCodes.LIST_UPDATE_FAIL)
    }

    override fun exists(id: Int, connection: Connection?): Boolean {
        connection ?: throw IllegalConnException()
        val statement = connection.prepareStatement(
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
