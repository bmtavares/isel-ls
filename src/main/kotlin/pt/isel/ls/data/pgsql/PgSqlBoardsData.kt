package pt.isel.ls.data.pgsql

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto

object PgSqlBoardsData : BoardsData {

    override val boardLists = PgSqlListsData
    override val cards = PgSqlCardsData
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
    fun getUserBoards(id:Int){
        PgDataContext.getConnection().use {

        }
    }

    override fun getUserBoards(user: User): List<Board> {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
                "select id, name, description from Boards b join UsersBoards ub on b.id = ub.boardId where ub.userId = ?;"
            )
            statement.setInt(1, user.id)

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

    override fun add(newBoard: InputBoardDto): Board {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Boards (name, description) values (?, ?) returning id;"
            )
            statement.setString(1, newBoard.name)
            statement.setString(2, newBoard.description)

            val rs = statement.executeQuery()

            while (rs.next()) {
                val id = rs.getInt("id")
                it.commit()
                return Board(id,newBoard.name,newBoard.description)
            }



                it.rollback()
                throw DataException("Failed to insert board.")



        }

    }

    override fun delete(id: Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete from Boards where id = ?;"
            )
            statement.setInt(1, id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete board.")
            }

            it.commit()
        }
    }

    override fun edit(editBoard: EditBoardDto) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Boards set description = ? where id = ?;"
            )
            statement.setString(1, editBoard.description)
            statement.setInt(2, editBoard.id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit board.")
            }

            it.commit()
        }
    }

    override fun exists(id:Int): Boolean {
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

    override fun addUserToBoard(user: User,board: Board){
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into usersboards (userid, boardid) values (?,?);"
            )
            statement.setInt(1, user.id)
            statement.setInt(2, board.id)
            statement.execute()
            it.commit()
        }

    }

    override fun getUsers(boardId: Int, user: User): List<User> {
        TODO("Not yet implemented")
    }

    operator fun invoke(): BoardsData {
        return this
    }
}
