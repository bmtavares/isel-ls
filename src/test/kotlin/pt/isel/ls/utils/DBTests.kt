package pt.isel.ls.utils

import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.*

class DBTests {
    private fun createDataSource(): PGSimpleDataSource {
        val dataSource = PGSimpleDataSource()
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        dataSource.setURL(jdbcDatabaseURL)
        return  dataSource
    }

    val dataSource = createDataSource()

    @Test
    fun select_test() {
        dataSource.getConnection().use {
            val stm = it.prepareStatement("select * from students")
            val rs = stm.executeQuery()


        }
    }
/*
    @Test
    fun insert_test() {

    }

    @Test
    fun insert_and_update_test() {

    }

    @Test
    fun insert_and_delete_test() {

    }
*/
}