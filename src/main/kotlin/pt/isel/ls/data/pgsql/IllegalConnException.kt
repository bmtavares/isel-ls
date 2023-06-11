package pt.isel.ls.data.pgsql

import pt.isel.ls.TaskAppException
import pt.isel.ls.utils.ErrorCodes

class IllegalConnException : TaskAppException(ErrorCodes.PGSQL_CONN_NULL, message = "A null PgSql Connection use was attempted.")
