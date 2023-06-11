package pt.isel.ls.data.pgsql

import pt.isel.ls.TaskAppException
import pt.isel.ls.utils.ErrorCodes

class UnexpectedSQLException(message: String?) : TaskAppException(ErrorCodes.PGSQL_UNEXPECTED, message = message)
