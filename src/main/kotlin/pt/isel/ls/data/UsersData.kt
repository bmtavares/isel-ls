package pt.isel.ls.data

import pt.isel.ls.data.entities.User
import java.util.UUID

interface UsersData : Data<User> {
    fun createToken(user: User): String
    fun getByToken(token: String): User
    fun getByEmail(email: String): User

}
