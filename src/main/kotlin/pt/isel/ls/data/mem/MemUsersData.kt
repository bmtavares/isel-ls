package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserToken
import java.sql.Timestamp
import java.util.*

object MemUsersData : MemGenericData<User>(emptyList<User>() as MutableList<User>), UsersData {
    private val usersList = mutableListOf<User>()
    private val userTokensList = mutableListOf<UserToken>()

    init {
        // Initialization code here
    }

    override fun createToken(user: User): UUID? {
        if (user.id == null)
            return null

        val newToken = UserToken(UUID.randomUUID(), user.id, Timestamp(System.currentTimeMillis()))
        userTokensList.add(newToken)
        return newToken.token
    }

    override fun getByToken(token: UUID): User? {
        val uId = userTokensList.find { ut -> ut.token == token } ?: return null
        return usersList.find { u -> u.id == uId.userId }
    }

    override fun getByEmail(email: String): User? = usersList.find { u -> u.email == email }

    operator fun invoke(): UsersData {
        return this
    }
}
