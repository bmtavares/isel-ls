package pt.isel.ls.data.pgsql

import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.TaskAppException
import pt.isel.ls.data.DataContext
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection
import java.sql.SQLException

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
            } catch (e: TaskAppException) {
                con.rollback()
                throw e
            } catch (e: SQLException) {
                con.rollback()
                throw UnexpectedSQLException(e.message)
            } catch (e: Exception) {
                con.rollback()
                throw TaskAppException(ErrorCodes.UNDEFINED, ErrorCodes.UNDEFINED.message, e.message)
            }
        }
    }
}
