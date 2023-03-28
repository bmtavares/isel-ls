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
    override fun createToken(user: User): String {
        PgDataContext.getConnection().use {
            it.autoCommit = false

            val statement = it.prepareStatement(
                "insert into UsersTokens (token, userId, creationDate) values (?,?,?);"
            )
            val token = UUID.randomUUID()
            statement.setObject(1, token)
            statement.setInt(2, user.id!!)
            statement.setTimestamp(3, Timestamp(System.currentTimeMillis()))

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to create token.")
            }

            it.commit()

            return token.toString()
        }
    }

    override fun getByToken(token: String): User {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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

            throw Exception("awdwa") // TODO
        }
    }

    override fun getByEmail(email: String): User {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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
    }

    override fun getById(id: Int): User {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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
    }

    override fun add(newUser: InputUserDto): User {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "insert into Users (name, email) values (?, ?) returning id,name,email;"
            )
            statement.setString(1, newUser.name)
            statement.setString(2, newUser.email)

            val rs = statement.executeQuery()
            while (rs.next()){
             val id = rs.getInt("id")
             val name = rs.getString("name")
             val email = rs.getString("email")
                it.commit()
                return User(id,name,email)
            }
                it.rollback()
                throw DataException("Failed to add user.")
            }
        }


    override fun delete(id:Int) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "delete from Users where id = ?;"
            )
            statement.setInt(1, id)

            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to delete user.")
            }

            it.commit()
        }
    }

    override fun edit(editUser: EditUserDto) {
        PgDataContext.getConnection().use {
            it.autoCommit = false
            val statement = it.prepareStatement(
                "update Users set name = ? where id = ?;")
            statement.setString(1, editUser.name)
            statement.setInt(2, editUser.id)


            val count = statement.executeUpdate()

            if (count == 0) {
                it.rollback()
                throw DataException("Failed to edit user.")
            }

            it.commit()
        }
    }

    override fun exists(id: Int): Boolean {
        PgDataContext.getConnection().use {
            val statement = it.prepareStatement(
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

    operator fun invoke(): UsersData {
        return this
    }
}