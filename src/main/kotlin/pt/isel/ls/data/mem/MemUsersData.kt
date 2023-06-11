package pt.isel.ls.data.mem

import pt.isel.ls.TaskAppException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserToken
import pt.isel.ls.tasksServices.dtos.CreateUserDto
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID

object MemUsersData : UsersData {
    override fun createToken(user: User, connection: Connection?): String {
        if (!MemDataSource.users.any { it == user }) throw TaskAppException(ErrorCodes.USER_READ_FAIL)
        val token = UUID.randomUUID()
        val ts = Timestamp(System.currentTimeMillis())
        MemDataSource.usersTokens.add(UserToken(token, user.id, ts))
        return token.toString()
    }

    override fun getByToken(token: String, connection: Connection?): User {
        val id = MemDataSource.usersTokens.firstOrNull { it.token.toString() == token }?.userId
            ?: throw TaskAppException(ErrorCodes.NO_TOKEN_MATCH)
        return MemDataSource.users.first { it.id == id }
    }

    override fun getByEmail(email: String, connection: Connection?): User =
        MemDataSource.users.firstOrNull { it.email == email }
            ?: throw TaskAppException(ErrorCodes.NO_EMAIL_MATCH)

    override fun add(newUser: CreateUserDto, connection: Connection?): User {
        if (MemDataSource.users.any { it.email == newUser.email }) throw TaskAppException(ErrorCodes.EMAIL_ALREADY_IN_USE)
        val newId = if (MemDataSource.users.isEmpty()) 1 else MemDataSource.users.maxOf { it.id } + 1
        val user = User(newId, newUser.name, newUser.email, newUser.passwordHash, newUser.salt)
        MemDataSource.users.add(user)
        return user
    }

    override fun edit(editUser: EditUserDto, connection: Connection?) {
        val oldUser = MemDataSource.users.firstOrNull { it.id == editUser.id } ?: throw TaskAppException(ErrorCodes.USER_READ_FAIL)
        val newUser = User(oldUser.id, editUser.name, oldUser.email, oldUser.passwordHash, oldUser.salt)
        MemDataSource.users.remove(oldUser)
        MemDataSource.users.add(newUser)
    }

    override fun getById(id: Int, connection: Connection?): User =
        MemDataSource.users.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.USER_READ_FAIL)

    override fun delete(id: Int, connection: Connection?) {
        val user = MemDataSource.users.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.USER_READ_FAIL)
        val userBoards = MemDataSource.usersBoards.filter { it.userId == id }
        if (userBoards.isNotEmpty()) {
            // For now unlink, find a better way to deal with orphan boards later
            MemDataSource.usersBoards.removeAll { it.userId == id }
        }
        MemDataSource.usersTokens.removeAll { it.userId == id }
        MemDataSource.users.remove(user)
    }

    override fun exists(id: Int, connection: Connection?): Boolean =
        MemDataSource.users.any { it.id == id }
}
