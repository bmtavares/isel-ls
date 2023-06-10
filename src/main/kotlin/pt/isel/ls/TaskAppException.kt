package pt.isel.ls

import org.http4k.core.Status
import pt.isel.ls.utils.ErrorCodes

open class TaskAppException(
    val errorCode: ErrorCodes = ErrorCodes.UNDEFINED,
    val status: Status = Status.INTERNAL_SERVER_ERROR,
    override val message: String = errorCode.message
) : Exception(message)
