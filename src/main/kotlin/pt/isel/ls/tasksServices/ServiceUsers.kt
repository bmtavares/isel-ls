package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataContext
import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.CreateUserDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
import pt.isel.ls.tasksServices.dtos.LoginUserDto
import pt.isel.ls.tasksServices.dtos.OutputUserDto
import pt.isel.ls.tasksServices.dtos.SecureOutputUserDto
import pt.isel.ls.tasksServices.users.ChallengeFailureException
import pt.isel.ls.utils.PasswordUtils

class ServiceUsers(private val context: DataContext, private val userRepository: UsersData) {
    fun createUser(newUser: InputUserDto): OutputUserDto {
        lateinit var user: User
        lateinit var token: String
        if (!EmailValidator.isEmailValid(newUser.email)) throw DataException("Invalid Email")

        val salt = PasswordUtils.generateSalt()
        val passwordHash = PasswordUtils.hashPassword(newUser.password, salt)

        val hashedNewUser = CreateUserDto(newUser.name, newUser.email, passwordHash, salt)

        try {
            context.handleData { con ->
                user = userRepository.add(hashedNewUser, con)
                token = userRepository.createToken(user, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return OutputUserDto(token, user.id, user.name)
    }

    fun getUser(userId: Int): SecureOutputUserDto {
        lateinit var user: User
        try {
            context.handleData { con ->
                user = userRepository.getById(userId, con)
            }
        } catch (e: Exception) {
            throw e
        }
        // Wipe out mentions of password and salt
        return SecureOutputUserDto(user.id, user.name, user.email)
    }

    fun getUserByToken(token: String): User {
        lateinit var user: User
        try {
            context.handleData { con ->
                user = userRepository.getByToken(token, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return user
    }

    fun authenticateUser(credentials: LoginUserDto): OutputUserDto {
        lateinit var user: User
        try {
            context.handleData {
                user = userRepository.getByEmail(credentials.email, it)
            }
        } catch (e: Exception) {
            throw e
        }

        val hashedPasswordAttempt = PasswordUtils.hashPassword(credentials.password, user.salt)
        if (hashedPasswordAttempt == user.passwordHash) {
            lateinit var token: String
            try {
                context.handleData {
                    token = userRepository.createToken(user, it)
                }
            } catch (e: Exception) {
                throw e
            }
            return OutputUserDto(token, user.id)
        }
        throw ChallengeFailureException()
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
