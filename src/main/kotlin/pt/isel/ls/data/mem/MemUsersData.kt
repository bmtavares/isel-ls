package pt.isel.ls.data.mem

import pt.isel.ls.data.EntityAlreadyExistsException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserToken
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import java.sql.Timestamp
import java.util.UUID

object MemUsersData : UsersData {
    override fun createToken(user: User): String {
        if (!MemDataSource.users.any { it == user }) throw EntityNotFoundException("User not found.", User::class)
        val token = UUID.randomUUID()
        val ts = Timestamp(System.currentTimeMillis())
        MemDataSource.usersTokens.add(UserToken(token, user.id, ts))
        return token.toString()
    }

    override fun getByToken(token: String): User {
        val id = MemDataSource.usersTokens.firstOrNull { it.token.toString() == token }?.userId
            ?: throw EntityNotFoundException("Could not match token to user.", User::class)
        return MemDataSource.users.first { it.id == id }
    }

    override fun getByEmail(email: String): User =
        MemDataSource.users.firstOrNull { it.email == email }
            ?: throw EntityNotFoundException("User not found.", User::class)

    override fun add(newUser: InputUserDto): User {
        if (MemDataSource.users.any { it.email == newUser.email }) throw EntityAlreadyExistsException("Email already in use.", User::class)
        val newId = if (MemDataSource.users.isEmpty()) 1 else MemDataSource.users.maxOf { it.id } + 1
        val user = User(newId, newUser.name, newUser.email)
        MemDataSource.users.add(user)
        return user
    }

    override fun edit(editUser: EditUserDto) {
        val oldUser = MemDataSource.users.firstOrNull { it.id == editUser.id } ?: throw EntityNotFoundException("User not found.", User::class)
        val newUser = User(oldUser.id, editUser.name, oldUser.email)
        MemDataSource.users.remove(oldUser)
        MemDataSource.users.add(newUser)
    }

    override fun getById(id: Int): User =
        MemDataSource.users.firstOrNull { it.id == id } ?: throw EntityNotFoundException("User not found.", User::class)

    override fun delete(id: Int) {
        val user = MemDataSource.users.firstOrNull { it.id == id } ?: throw EntityNotFoundException("User not found.", User::class)
        val userBoards = MemDataSource.usersBoards.filter { it.userId == id }
        if(userBoards.isNotEmpty()) {
            // For now unlink, find a better way to deal with orphan boards later
            MemDataSource.usersBoards.removeAll { it.userId == id }
        }
        MemDataSource.usersTokens.removeAll { it.userId == id }
        MemDataSource.users.remove(user)
    }

    override fun exists(id: Int): Boolean =
        MemDataSource.users.any { it.id == id }
}
