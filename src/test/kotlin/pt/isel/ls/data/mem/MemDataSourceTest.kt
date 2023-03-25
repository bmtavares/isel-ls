package pt.isel.ls.data.mem

import pt.isel.ls.data.entities.User
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemDataSourceTest {
    @BeforeTest
    fun clearStorage() {
//        MemDataSource.clearStorage()
    }

    @Test
    fun addUser() {
//        assertNull(MemDataSource.users.getById(1))
//        val newUser = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//        assertEquals(newUser, User(1, "Alberto", "alberto@example.org"))
    }

    @Test
    fun getUserByEmail() {
//        val newUser1 = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//        val newUser2 = MemDataSource.users.add(
//            User(
//                null,
//                name = "Mariana",
//                email = "mariana@example.org"
//            )
//        )
//        assertEquals(
//            newUser1.clone(1),
//            MemDataSource
//                .users.getByEmail(newUser1.email)
//        )
//        assertEquals(
//            newUser2.clone(2),
//            MemDataSource
//                .users.getByEmail(newUser2.email)
//        )
    }

    @Test
    fun getUserByToken() {
//        val newUser = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//
//        val token = MemDataSource.users.createToken(newUser)
//
//        assertNotNull(token)
//
//        val returnedUser = MemDataSource.users.getByToken(token)
//
//        assertEquals(newUser, returnedUser)
    }

    @Test
    fun existsUser() {
//        val newUser = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//
//        assertTrue(MemDataSource.users.exists(newUser))
    }

    @Test
    fun updateUser() {
//        val newUser = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//
//        newUser.email = "alberto23@example.org"
//
//        MemDataSource.users.edit(newUser)
//
//        assertEquals(
//            newUser,
//            MemDataSource.users.getByEmail(newUser.email)
//        )
    }

    @Test
    fun deleteUser() {
//        val newUser = MemDataSource.users.add(
//            User(
//                null,
//                name = "Alberto",
//                email = "alberto@example.org"
//            )
//        )
//
//        assertEquals(
//            newUser,
//            MemDataSource.users.getByEmail(newUser.email)
//        )
//
//        MemDataSource.users.delete(newUser)
//
//        assertNull(MemDataSource.users.getByEmail(newUser.email))
    }
}
