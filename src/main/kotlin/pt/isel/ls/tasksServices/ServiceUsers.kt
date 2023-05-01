package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.pgsql.PgDataContext.handleDB
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto

class ServiceUsers(private val userRepository: UsersData) {
    fun createUser(newUser: InputUserDto): OutputUserDto {
        lateinit var user: User
        lateinit var token: String
        if (!EmailValidator.isEmailValid(newUser.email)) throw DataException("Invalid Email")
        try {
            handleDB { con ->
                user = userRepository.add(newUser, con)
                token = userRepository.createToken(user, con)
            }
        } catch (e: Exception) {
            throw DataException("")
        }
        return OutputUserDto(token, user.id)
    }

    fun getUser(userId: Int): User {
        lateinit var user: User
        try {
            handleDB { con ->
                user = userRepository.getById(userId, con)
            }
        } catch (e: Exception) {
            throw DataException("")
        }
        return user
    }

    fun getUserByToken(token: String): User {
        lateinit var user: User
        try {
            handleDB { con ->
                user = userRepository.getByToken(token, con)
            }
        } catch (e: Exception) {
            throw DataException("")
        }
        return user
    }

    class EmailValidator {
        companion object {
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            fun isEmailValid(email: String): Boolean {
                return EMAIL_REGEX.toRegex().matches(email)
            }
        }
    }
}
