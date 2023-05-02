package pt.isel.ls.data.pgsql

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.data.DataContext
import java.sql.Connection

object PgDataContext : DataContext {
    private val dataSource: PGSimpleDataSource = PGSimpleDataSource()

    init {
        dataSource.setUrl(System.getenv("JDBC_DATABASE_URL"))
    }

    fun getConnection() = dataSource.connection

    override fun handleData(block: (Connection?) -> Unit) {
        getConnection().use { con ->
            con.autoCommit = false
            try {
                block(con).also { con.commit() }
            } catch (e: Exception) {
                con.rollback()
                throw e
            }
        }
    }
}
