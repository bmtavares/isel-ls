package pt.isel.ls.data.pgsql

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import java.sql.Connection

object PgSqlBoardsData : BoardsData {
    override fun getByName(name: String,connection : Connection?): Board {
        checkNotNull(connection){"Connection is need to use DB"}
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

            throw Exception("awdwa") // TODO

    }

    override fun getUserBoards(user: User, limit: Int, skip: Int,connection : Connection?): List<Board> {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "select id, name, description from Boards b join UsersBoards ub on b.id = ub.boardId where ub.userId = ? offset ? limit ?;"
            )
            statement.setInt(1, user.id)
            statement.setInt(2, skip)
            statement.setInt(3, limit)

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

    override fun getById(id: Int,connection : Connection?): Board {
        checkNotNull(connection){"Connection is need to use DB"}
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

            throw Exception("SQL exception") // TODO

    }

    override fun add(newBoard: InputBoardDto,connection : Connection?): Board {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "insert into Boards (name, description) values (?, ?) returning id;"
            )
            statement.setString(1, newBoard.name)
            statement.setString(2, newBoard.description)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                return Board(id, newBoard.name, newBoard.description)
            }

            throw DataException("Failed to insert board.")
    }

    override fun delete(id: Int,connection : Connection?) {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "delete from Boards where id = ?;"
            )
            statement.setInt(1, id)

            val count = statement.executeUpdate()

            if (count == 0) {
                throw DataException("Failed to delete board.")
            }
    }

    override fun edit(editBoard: EditBoardDto,connection : Connection?) {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "update Boards set description = ? where id = ?;"
            )
            statement.setString(1, editBoard.description)
            statement.setInt(2, editBoard.id)

            val count = statement.executeUpdate()

            if (count == 0) {
                throw DataException("Failed to edit board.")
            }

    }

    override fun exists(id: Int,connection : Connection?): Boolean {
        checkNotNull(connection){"Connection is need to use DB"}
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

    override fun addUserToBoard(userId: Int, boardId: Int,connection : Connection?) {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "insert into usersboards (userid, boardid) values (?,?);"
            )
            statement.setInt(1, userId)
            statement.setInt(2, boardId)
            statement.execute()


    }

    override fun getUsers(boardId: Int, user: User, limit: Int, skip: Int,connection : Connection?): List<User> {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "select u.id, u.name, u.email from Users u join UsersBoards ub on u.id = ub.userid where ub.boardid = ? offset ? limit ?;"
            )
            statement.setInt(1, boardId)
            statement.setInt(2, skip)
            statement.setInt(3, limit)

            val rs = statement.executeQuery()

            val results = mutableListOf<User>()

            while (rs.next()) {
                results += User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                )
            }
            return results

    }

    override fun deleteUserFromBoard(userId: Int, boardId: Int,connection : Connection?) {
        checkNotNull(connection){"Connection is need to use DB"}
            val statement = connection.prepareStatement(
                "delete from usersboards where userid=? and boardid=?;"
            )
            statement.setInt(1, userId)
            statement.setInt(2, boardId)
            statement.execute()


    }

    operator fun invoke(): BoardsData {
        return this
    }
}
