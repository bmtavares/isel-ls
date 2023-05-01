package pt.isel.ls.data.pgsql

import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
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

        if (count == 0) {
            throw DataException("Failed to create token.")
        }
        return token.toString()
    }

    override fun getByToken(token: String, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select id, name, email from Users u join UsersTokens ut on u.id = ut.userId where token = ?;"
        )
        statement.setString(1, token)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            )
        }

        throw Exception("awdwa") // TODO
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
                rs.getString("email")
            )
        }

        throw Exception("failed to get by email") // TODO
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
                rs.getString("email")
            )
        }

        throw Exception("failed to get by id") // TODO
    }

    override fun add(newUser: InputUserDto, connection: Connection?): User {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "insert into Users (name, email) values (?, ?) returning id,name,email;"
        )
        statement.setString(1, newUser.name)
        statement.setString(2, newUser.email)

        val rs = statement.executeQuery()
        while (rs.next()) {
            val id = rs.getInt("id")
            return User(id, newUser.name, newUser.email)
        }
        throw DataException("Failed to add user.")
    }

    override fun delete(id: Int, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "delete from Users where id = ?;"
        )
        statement.setInt(1, id)

        val count = statement.executeUpdate()

        if (count == 0) {
            throw DataException("Failed to delete user.")
        }
    }

    override fun edit(editUser: EditUserDto, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "update Users set name = ? where id = ?;"
        )
        statement.setString(1, editUser.name)
        statement.setInt(2, editUser.id)

        val count = statement.executeUpdate()

        if (count == 0) {
            throw DataException("Failed to edit user.")
        }
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

    operator fun invoke(): UsersData {
        return this
    }
}
