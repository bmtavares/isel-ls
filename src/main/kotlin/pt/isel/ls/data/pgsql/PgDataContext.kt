package pt.isel.ls.data.pgsql

import org.postgresql.ds.PGSimpleDataSource

object PgDataContext {
    private val dataSource: PGSimpleDataSource = PGSimpleDataSource()

    init {
        dataSource.setUrl(System.getenv("JDBC_DATABASE_URL"))
    }

    fun getConnection() = dataSource.connection
}