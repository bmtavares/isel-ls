package pt.isel.ls.storage.DB

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.server.Board
import pt.isel.ls.server.BoardList
import pt.isel.ls.server.User
import pt.isel.ls.storage.BoardStorage

class DBBoard:BoardStorage {

    var dataSource: PGSimpleDataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL: String = System.getenv("JDBC_DATABASE_URL")
    init{
        dataSource.setURL(jdbcDatabaseURL)
    }

    override fun createBoard(name: String, description: String): Int {
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into boards (name, description)values ('$name','$description')")
            stm.execute()
            val stm2 = it.prepareStatement("select id from boards where name = '$name'")
            val rs2 = stm2.executeQuery()
            if (!rs2.next()){throw Exception("failed to insert board")}
            return rs2.getInt("id")
        }
    }

    override fun addUser(userId: Int, boardId: Int) {
        TODO("Not yet implemented")
    }

    override fun getBoardDetails(boardId: Int,user:User): Board? {
        dataSource.connection.use {
            it.autoCommit = false
            val boards = mutableListOf<Board>()
            val stm = it.prepareStatement("SELECT b.* FROM boards b " +
                    "JOIN usersboards ub ON b.id = ub.boardid" +
                    " WHERE b.id= $boardId and ub.userid = ${user.id}")
            val rs = stm.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val description = rs.getString("description")
                boards.add(Board(id,name,description))
            }
            check(boards.size == 1){
                return null
            }
            return boards.first()
        }
    }

    override fun getUsers(boardId: Int,user:User):List<User> {
        dataSource.connection.use {
            it.autoCommit = false
            val users = mutableListOf<User>()
            val stm = it.prepareStatement("SELECT u.* FROM users u " +
                    "JOIN usersboards ub ON u.id = ub.userid" +
                    " WHERE ub.boardid= '$boardId' ")
            val rs = stm.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val email = rs.getString("email")
                users.add(User(id,name,email))
            }
            return users
        }
    }

    override fun createNewList(name: String): Int {
        TODO("Not yet implemented")
    }

    override fun getLists(boardId: Int,user:User): List<BoardList> {
        TODO("Not yet implemented")
    }

    override fun getListDetails(boardId: Int, listId: Int) {
        TODO("Not yet implemented")
    }

    override fun createCard(boardId: Int, listId: Int, name: String, description: String, dueDate: String): Int {
        TODO("Not yet implemented")
    }

    override fun getCardsonList() {
        TODO("Not yet implemented")
    }
}