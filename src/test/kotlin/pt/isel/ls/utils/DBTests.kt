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

    lateinit var dataSource : PGSimpleDataSource


    init{
        dataSource= createDataSource()
    }

    @Test
    fun select_test() {
        dataSource.getConnection().use {
            val stm = it.prepareStatement("select * from students")
            val rs = stm.executeQuery()
            assert(rs != null)
        }
    }

    @Test
    fun insert_test() {

    }
/*
    @Test
    fun insert_and_update_test() {

    }
*/
    @Test
    fun insert_and_delete_test() {

    dataSource.getConnection().use {
        it.autoCommit = false
        val stm = it.prepareStatement("insert into students(course, number, name) values (1, 12, 'mm')")
        stm.execute()
        val delt_stm = it.prepareStatement("delete from students where name = 'mm'")
        delt_stm.execute()
        val l_stm = it.prepareStatement("seltect * from students where name = 'mm'")
        assertFailsWith<org.postgresql.util.PSQLException>{
            val l_rs = stm.executeQuery()
        }


        it.rollback()
    }

    }

}