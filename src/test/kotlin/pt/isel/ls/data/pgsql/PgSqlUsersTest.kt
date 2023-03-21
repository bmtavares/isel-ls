package pt.isel.ls.data.pgsql

import org.junit.jupiter.api.Test
import pt.isel.ls.data.entities.User
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class PgSqlUsersTest {
    @BeforeTest
    fun dropDatabase() {
        PgDataContext.getConnection().use {
            it.prepareStatement(
                "delete from UsersTokens;" +
                        "delete from UsersBoards;" +
                        "delete from Cards;" +
                        "delete from Lists;" +
                        "delete from Boards;" +
                        "delete from Users;"
            ).execute()
        }
    }

    fun createTestUser(name: String = "Alberto") = User(null, name, "${name.lowercase()}@example.org")

    @Test
    fun insertUser() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val newUser = createTestUser()

            val createdUser = src.users.add(newUser)

            assertEquals(newUser.email, createdUser.email)
        }
    }

    @Test
    fun insertAndFetchMultipleUsers() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val newUser1 = createTestUser()
            val newUser2 = createTestUser("Maria")
            val newUser3 = createTestUser("Jose")

            val createdUser1 = src.users.add(newUser1)
            val createdUser2 = src.users.add(newUser2)
            val createdUser3 = src.users.add(newUser3)

            val fetchedUser2 = src.users.getByEmail(newUser2.email)
            val fetchedUser1 = src.users.getByEmail(newUser1.email)
            val fetchedUser3 = src.users.getByEmail(newUser3.email)

            assertNotEquals(createdUser1, fetchedUser2)
            assertNotEquals(createdUser2, fetchedUser1)
            assertEquals(createdUser3, fetchedUser3)
        }
    }

    @Test
    fun getByIdAfterInsertUser() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            assertNotNull(createdUser.id)
            val fetchedUser = src.users.getById(createdUser.id!!)

            assertEquals(createdUser, fetchedUser)
        }
    }

    @Test
    fun getByEmailAfterInsertUser() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            val fetchedUser = src.users.getByEmail(createdUser.email)

            assertEquals(createdUser, fetchedUser)
        }
    }

    @Test
    fun createAndCheckUserToken() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            val userToken = src.users.createToken(createdUser)

            assertNotNull(userToken)

            val fetchedUser = src.users.getByToken(userToken)

            assertEquals(createdUser, fetchedUser)
        }
    }

    @Test
    fun checkIfUserExists() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            assert(src.users.exists(createdUser))
        }
    }

    @Test
    fun deleteUser() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            src.users.delete(createdUser)

            assert(!src.users.exists(createdUser))
        }
    }

    @Test
    fun editUser() {
        PgDataContext.getConnection().use {
            val src = PgSqlDataSource(it)

            val createdUser = src.users.add(createTestUser())

            createdUser.name = "Maria"
            createdUser.email = "maria@example.org"

            src.users.edit(createdUser)

            val fetchedUser = src.users.getByEmail(createdUser.email)

            assertNotNull(fetchedUser)

            assertEquals(createdUser, fetchedUser)
        }
    }
}
