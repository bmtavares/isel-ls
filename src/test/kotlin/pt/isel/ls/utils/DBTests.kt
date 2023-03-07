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
     private val dataSource : PGSimpleDataSource = createDataSource()


    @Test
    fun select_test() {
        dataSource.connection.use {
            // assumption that students is not null
            val stm = it.prepareStatement("select * from students")
            val rs = stm.executeQuery()
            assert(rs != null)
        }
    }

    @Test
    fun insert_test() {
        dataSource.connection.use {
            it.autoCommit = false
            var statement ="INSERT INTO students (name,number,course) VALUES ('John Doe', 999999, 1);"
            var stm = it.prepareStatement(statement)
            val rs = stm.execute()
            assertEquals(rs,false)
            statement = "SELECT * from students WHERE number=999999;"
            stm = it.prepareStatement(statement)
            val rs2 = stm.executeQuery()
            rs2.next()
            val number = rs2.getInt("number")
            assertEquals(number,999999)
            it.rollback()
        }
    }

    @Test
    fun insert_and_update_test() {
        dataSource.connection.use {
                it.autoCommit = false
                var statement ="INSERT INTO students (name,number,course) VALUES ('John Doe', 999999, 1);"
                var stm = it.prepareStatement(statement)
                val rs = stm.execute()
                assertEquals(rs,false)
                statement = "UPDATE students\n" +
                        "SET name = 'Sérgio'\n" +
                        "WHERE number = 999999;\n"
                stm = it.prepareStatement(statement)
                val rs2 = stm.execute()
                statement = "SELECT * from students WHERE number=999999;"
                stm = it.prepareStatement(statement)
                val rs3 = stm.executeQuery()
                rs3.next()
                val name = rs3.getString("name")
                assertEquals(name,"Sérgio")
                it.rollback()
            }
    }

    @Test
    fun insert_and_delete_test() {

    dataSource.connection.use {
        it.autoCommit = false
        val stm = it.prepareStatement("insert into students(course, number, name) values (1, 12, 'mm')")
        stm.execute()
        val delt_stm = it.prepareStatement("delete from students where name = 'mm'")
        delt_stm.execute()
        val l_stm = it.prepareStatement("seltect * from students where name = 'mm'")
        assertFailsWith<org.postgresql.util.PSQLException>{
            l_stm.executeQuery()
        }
        it.rollback()
    }

    }

}