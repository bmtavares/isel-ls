package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto

class ServiceUsers(private val userRepository: UsersData) {
    fun createUser(newUser: InputUserDto): OutputUserDto {
        checkNotNull(newUser.email)
        if (!EmailValidator.isEmailValid(newUser.email)) throw DataException("Invalid Email")
        return try {
            val user = userRepository.add(newUser)
            val token = userRepository.createToken(user)
            OutputUserDto(token, user.id)

        } catch (e: Exception) {
            throw DataException("")
        }
    }

    fun getUser(userId: Int): User {
        return try {
            userRepository.getById(userId)
        } catch (e: Exception) {
            throw DataException("")
        }
    }

    fun getUserByToken(token: String): User {
        return try {
            userRepository.getByToken(token)
        } catch (e: Exception) {
            throw DataException("")
        }

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