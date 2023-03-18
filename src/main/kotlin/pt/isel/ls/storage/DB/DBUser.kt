package pt.isel.ls.storage.DB

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage
import pt.isel.ls.tasksServices.UserResponses
import java.util.*

class DBUser: UserStorage {
    var dataSource: PGSimpleDataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL: String = System.getenv("JDBC_DATABASE_URL")
    init{
        dataSource.setURL(jdbcDatabaseURL)
    }


    override fun createUser(user: User):UserResponses{
        dataSource.connection.use {
            it.autoCommit = false
            val stm = it.prepareStatement("INSERT INTO Users (name,email) VALUES (${user.name}, ${user.email});")
            val rs = stm.execute()
            if (!rs) throw Exception("Failed")
            else return UserResponses.Created
        }
    }

    override fun getUser(userId:Int): Pair<User?,UserResponses>? {
        dataSource.connection.use {
            it.autoCommit = false
            val users = mutableListOf<User>()
            val stm = it.prepareStatement("select * from users where $userId = id")
            val rs = stm.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val email = rs.getString("email")
                users.add(User(id,name,email))
            }
            check(users.size == 1){
                return null
            }
            return Pair(users.first(),UserResponses.Ok)
        }

    }

    override fun getUserByToken(token: String): Pair<User?,UserResponses>?{
        dataSource.connection.use {
            val users = mutableListOf<User>()
            val stm = it.prepareStatement("SELECT u.* FROM Users u JOIN UsersTokens ut ON u.id = ut.userId WHERE ut.token = $token")
            val rs = stm.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val email = rs.getString("email")
                users.add(User(id,name,email))
            }
            check(users.size == 1){
                return null
            }
            return Pair(users.first(),UserResponses.Ok)
        }
    }
}