package pt.isel.ls.data.pgsql

import org.postgresql.util.PSQLException
import pt.isel.ls.TaskAppException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.CreateUserDto
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID

object PgSqlUsersData : UsersData {
    override fun createToken(user: User, connection: Connection?): String {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "insert into UsersTokens (token, userId, creationDate) values (?,?,?);"
        )
        val token = UUID.randomUUID()
        statement.setObject(1, token)
        statement.setInt(2, user.id)
        statement.setTimestamp(3, Timestamp(System.currentTimeMillis()))

        val count = statement.executeUpdate()

        if (count == 0) throw TaskAppException(ErrorCodes.TOKEN_GENERATION_FAILED)
        return token.toString()
    }

    override fun getByToken(token: String, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select u.id, u.name, u.email, u.passwordHash, u.salt from Users u join UsersTokens ut on u.id = ut.userId where token = ?;"
        )
        statement.setString(1, token)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("passwordHash"),
                rs.getString("salt")
            )
        }

        throw TaskAppException(ErrorCodes.NO_TOKEN_MATCH)
    }

    override fun getByEmail(email: String, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select * from Users where email = ?;"
        )
        statement.setString(1, email)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("passwordHash"),
                rs.getString("salt")
            )
        }

        throw TaskAppException(ErrorCodes.NO_EMAIL_MATCH)
    }

    override fun getById(id: Int, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select * from Users where id = ?;"
        )
        statement.setInt(1, id)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("passwordHash"),
                rs.getString("salt")
            )
        }

        throw TaskAppException(ErrorCodes.USER_READ_FAIL)
    }

    override fun add(newUser: CreateUserDto, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "insert into Users (name, email, passwordHash, salt) values (?, ?, ?, ?) returning id,name,email;"
        )
        statement.setString(1, newUser.name)
        statement.setString(2, newUser.email)
        statement.setString(3, newUser.passwordHash)
        statement.setString(4, newUser.salt)

        try {
            val rs = statement.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                return User(id, newUser.name, newUser.email, newUser.passwordHash, newUser.salt)
            }
        } catch (e: PSQLException) {
            if (e.sqlState == "23505") {
                throw TaskAppException(ErrorCodes.EMAIL_ALREADY_IN_USE)
            } else {
                throw TaskAppException(ErrorCodes.USER_CREATE_FAIL, message = e.message)
            }
        }

        throw TaskAppException(ErrorCodes.USER_CREATE_FAIL)
    }

    override fun delete(id: Int, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "delete from Users where id = ?;"
        )
        statement.setInt(1, id)

        val count = statement.executeUpdate()

        if (count == 0) throw TaskAppException(ErrorCodes.USER_DELETE_FAIL)
    }

    override fun edit(editUser: EditUserDto, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "update Users set name = ? where id = ?;"
        )
        statement.setString(1, editUser.name)
        statement.setInt(2, editUser.id)

        val count = statement.executeUpdate()

        if (count == 0) throw TaskAppException(ErrorCodes.USER_UPDATE_FAIL)
    }

    override fun exists(id: Int, connection: Connection?): Boolean {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select count(*) exists from Users where id = ?;"
        )
        statement.setInt(1, id)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return rs.getInt("exists") == 1
        }

        return false
    }
}
