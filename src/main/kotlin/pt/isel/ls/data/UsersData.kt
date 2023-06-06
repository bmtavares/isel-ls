package pt.isel.ls.data

import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.CreateUserDto
import pt.isel.ls.tasksServices.dtos.EditUserDto
import java.sql.Connection

interface UsersData : Data<User> {
    fun createToken(user: User, connection: Connection? = null): String
    fun getByToken(token: String, connection: Connection? = null): User
    fun getByEmail(email: String, connection: Connection? = null): User

    fun add(newUser: CreateUserDto, connection: Connection? = null): User

    fun edit(editUser: EditUserDto, connection: Connection? = null)
}
