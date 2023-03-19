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


    override fun createUser(email:String,name:String):Pair<UserResponses,String?>{
        dataSource.connection.use {
            it.autoCommit = false
            val stm = it.prepareStatement("INSERT INTO Users (name,email) VALUES ('$name', '$email');")
            val rs = stm.execute()
            val stm2 = it.prepareStatement("select * from users u where '$email' = u.email")
            val rs2 = stm2.executeQuery()
            val users = mutableListOf<User>()
            while (rs2.next()){
                val idRs2 = rs2.getInt("id")
                val nameRs2 = rs2.getString("name")
                val emailRs2 = rs2.getString("email")
                users.add(User(idRs2,nameRs2,emailRs2))
            }
            check(users.size == 1){
                return Pair(UserResponses.InvalidUser,null)
            }
            val uuid = UUID.randomUUID().toString()
            val user = users.first()
            val stm3 = it.prepareStatement("insert into userstokens (token, userid, creationdate) values ('$uuid',${user.id},now())")
            val rs3 = stm3.execute()
            it.commit()
            return Pair(UserResponses.Created,"{userId:${user.id},token:$uuid}")
        }
    }

    override fun getUser(userId:Int): Pair<User?,UserResponses>? {
        dataSource.connection.use {
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