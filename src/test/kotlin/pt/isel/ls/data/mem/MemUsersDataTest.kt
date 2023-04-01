package pt.isel.ls.data.mem

import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.tasksServices.dtos.EditUserDto
import pt.isel.ls.tasksServices.dtos.InputUserDto
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
        assertFailsWith<EntityNotFoundException>{MemUsersData.getById(1)}
        val newUser = InputUserDto(
            "Alberto",
            "alberto@example.org"
        )
        val insertedUser = MemUsersData.add(newUser)
        assertEquals(newUser.email, insertedUser.email)
    }

    @Test
    fun getUserByEmail() {
        val newUser1 = InputUserDto(
                "Alberto",
                "alberto@example.org"
            )
        val newUser2 = InputUserDto(
                "Mariana",
                "mariana@example.org"
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
        val newUser = InputUserDto(
            "Alberto",
            "alberto@example.org"
        )

        val insertedUser = MemUsersData.add(newUser)

        val token = MemUsersData.createToken(insertedUser)

        val tokenUser = MemUsersData.getByToken(token)

        assertEquals(insertedUser, tokenUser)
    }

    @Test
    fun existsUser() {
        val newUser = InputUserDto(
            "Alberto",
            "alberto@example.org"
        )

        val insertedUser = MemUsersData.add(newUser)

        assertTrue(MemUsersData.exists(insertedUser.id))
        assertFalse(MemUsersData.exists(insertedUser.id+1))
    }

    @Test
    fun updateUser() {
        val newUser = InputUserDto(
            "Alberto",
            "alberto@example.org"
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
        val newUser = InputUserDto(
            "Alberto",
            "alberto@example.org"
        )

        val insertedUser = MemUsersData.add(newUser)

        val gottenUser = MemUsersData.getById(insertedUser.id)
        assertEquals(insertedUser, gottenUser)

        MemUsersData.delete(insertedUser.id)

        assertFailsWith<EntityNotFoundException> { MemUsersData.delete(insertedUser.id) }
    }
}
