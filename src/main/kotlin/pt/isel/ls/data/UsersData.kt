package pt.isel.ls.data

import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.tasksServices.dtos.InputUserDto

interface UsersData : Data<User> {
    fun createToken(user: User): String
    fun getByToken(token: String): User
    fun getByEmail(email: String): User

    fun add(newUser:InputUserDto):User

    fun edit(editUser: EditUserDto)

}
