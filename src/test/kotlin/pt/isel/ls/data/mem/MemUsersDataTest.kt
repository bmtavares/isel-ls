package pt.isel.ls.data.mem

import pt.isel.ls.TaskAppException
import pt.isel.ls.tasksServices.dtos.CreateUserDto
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.utils.PasswordUtils
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MemUsersDataTest {
    @BeforeTest
    fun clearStorage() {
        MemDataSource.clearStorage()
    }

    @Test
    fun addUser() {
        assertFailsWith<TaskAppException> {
            MemUsersData.getById(1)
        }
        val salt = PasswordUtils.generateSalt()
        val newUser = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt),
            salt
        )
        val insertedUser = MemUsersData.add(newUser)
        assertEquals(newUser.email, insertedUser.email)
    }

    @Test
    fun getUserByEmail() {
        val salt1 = PasswordUtils.generateSalt()
        val newUser1 = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt1),
            salt1
        )
        val salt2 = PasswordUtils.generateSalt()
        val newUser2 = CreateUserDto(
            "Mariana",
            "mariana@example.org",
            PasswordUtils.hashPassword("olamundo", salt2),
            salt2
        )

        val insertedUser1 = MemUsersData.add(newUser1)
        val insertedUser2 = MemUsersData.add(newUser2)

        val emailUser1 = MemUsersData.getByEmail(newUser1.email)
        val emailUser2 = MemUsersData.getByEmail(newUser2.email)

        assertEquals(insertedUser1.email, emailUser1.email)
        assertEquals(insertedUser2.email, emailUser2.email)
    }

    @Test
    fun getUserByToken() {
        val salt = PasswordUtils.generateSalt()
        val newUser = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt),
            salt
        )

        val insertedUser = MemUsersData.add(newUser)

        val token = MemUsersData.createToken(insertedUser)

        val tokenUser = MemUsersData.getByToken(token)

        assertEquals(insertedUser, tokenUser)
    }

    @Test
    fun existsUser() {
        val salt = PasswordUtils.generateSalt()
        val newUser = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt),
            salt
        )

        val insertedUser = MemUsersData.add(newUser)

        assertTrue(MemUsersData.exists(insertedUser.id))
        assertFalse(MemUsersData.exists(insertedUser.id + 1))
    }

    @Test
    fun updateUser() {
        val salt = PasswordUtils.generateSalt()
        val newUser = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt),
            salt
        )

        val insertedUser = MemUsersData.add(newUser)

        val requestEditUser = EditUserDto(insertedUser.id, "Mariana")

        MemUsersData.edit(requestEditUser)

        val edittedUser = MemUsersData.getById(insertedUser.id)

        assertNotEquals(edittedUser.name, insertedUser.name)
        assertEquals(edittedUser.name, "Mariana")
    }

    @Test
    fun deleteUser() {
        val salt = PasswordUtils.generateSalt()
        val newUser = CreateUserDto(
            "Alberto",
            "alberto@example.org",
            PasswordUtils.hashPassword("olamundo", salt),
            salt
        )

        val insertedUser = MemUsersData.add(newUser)

        val gottenUser = MemUsersData.getById(insertedUser.id)
        assertEquals(insertedUser, gottenUser)

        MemUsersData.delete(insertedUser.id)

        assertFailsWith<TaskAppException> { MemUsersData.delete(insertedUser.id) }
    }
}
