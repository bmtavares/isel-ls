package pt.isel.ls

import pt.isel.ls.utils.ErrorCodes

open class TaskAppException(
    val errorCode: ErrorCodes = ErrorCodes.UNDEFINED,
    val issue: String? = null,
    override val message: String? = errorCode.message
) : Exception(message)
