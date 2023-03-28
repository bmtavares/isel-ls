package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserToken
import java.sql.Timestamp
import java.util.*

object MemUsersData : MemGenericData<User>(mutableListOf<User>()), UsersData {
    private val usersList = mutableListOf<User>()
    private val userTokensList = mutableListOf<UserToken>()

    init {
        // Initialization code here
    }

    override fun createToken(user: User): String {
        val newToken = UserToken(UUID.randomUUID(), user.id!!, Timestamp(System.currentTimeMillis()))
        userTokensList.add(newToken)
        return newToken.token.toString()
    }



    override fun getByToken(token: String): User {
        val uId = userTokensList.first { it.toString() == token }
        return usersList.first { u -> u.id == uId.userId }
    }

    override fun getByEmail(email: String): User = usersList.first { u -> u.email == email }

    operator fun invoke(): UsersData {
        return this
    }
}
