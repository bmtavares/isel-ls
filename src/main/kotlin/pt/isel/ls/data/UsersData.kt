package pt.isel.ls.data

import pt.isel.ls.data.entities.User
import java.util.UUID

interface UsersData : Data<User> {
    fun createToken(user: User): UUID?
    fun getByToken(token: UUID): User?
    fun getByEmail(email: String): User?
}
