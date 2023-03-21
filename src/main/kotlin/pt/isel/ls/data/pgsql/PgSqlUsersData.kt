package pt.isel.ls.data.pgsql

import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import java.sql.Connection
import java.sql.Timestamp
import java.util.UUID

class PgSqlUsersData(private val connection: Connection) : UsersData {
    override fun createToken(user: User): UUID? {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false

        val statement = connection.prepareStatement(
            "insert into UsersTokens (token, userId, creationDate) values (?,?,?);"
        )
        val token = UUID.randomUUID()
        statement.setObject(1, token)
        statement.setInt(2, user.id!!)
        statement.setTimestamp(3, Timestamp(System.currentTimeMillis()))

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to create token.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState

        return token
    }

    override fun getByToken(token: UUID): User? {
        val statement = connection.prepareStatement(
            "select id, name, email from Users u join UsersTokens ut on u.id = ut.userId where token = ?;"
        )
        statement.setObject(1, token)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            )
        }

        return null
    }

    override fun getByEmail(email: String): User? {
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

        return null
    }

    override fun getById(id: Int): User? {
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

        return null
    }

    override fun add(entity: User): User {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "insert into Users (name, email) values (?, ?);"
        )
        statement.setString(1, entity.name)
        statement.setString(2, entity.email)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to add user.")
        }

        val newUser = getByEmail(entity.email)

        if (newUser == null) {
            connection.rollback()
            throw DataException("Failed to retrieve user after adding.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState

        return newUser
    }

    override fun delete(entity: User) {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "delete from Users where id = ?;"
        )
        statement.setInt(1, entity.id!!)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to delete user.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState
    }

    override fun edit(entity: User) {
        val previousCommitState = connection.autoCommit
        connection.autoCommit = false
        val statement = connection.prepareStatement(
            "update Users set name = ? where id = ?;" +
                    "update Users set email = ? where id = ?;"
        )
        statement.setString(1, entity.name)
        statement.setString(3, entity.email)
        statement.setInt(2, entity.id!!)
        statement.setInt(4, entity.id)

        val count = statement.executeUpdate()

        if (count == 0) {
            connection.rollback()
            throw DataException("Failed to edit user.")
        }

        connection.commit()
        connection.autoCommit = previousCommitState
    }

    override fun exists(entity: User): Boolean {
        val statement = connection.prepareStatement(
            "select count(*) exists from Users where id = ?;"
        )
        statement.setInt(1, entity.id!!)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return rs.getInt("exists") == 1
        }

        return false
    }
}