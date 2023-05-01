package pt.isel.ls.data.pgsql

import org.postgresql.ds.PGSimpleDataSource
import java.sql.Connection

object PgDataContext {
    private val dataSource: PGSimpleDataSource = PGSimpleDataSource()

    init {
        dataSource.setUrl(System.getenv("JDBC_DATABASE_URL"))
    }

    fun getConnection() = dataSource.connection

    fun handleDB(block: (Connection) -> Unit) {
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
