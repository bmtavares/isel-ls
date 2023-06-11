package pt.isel.ls.utils

import org.http4k.core.Request
import org.http4k.routing.path
import pt.isel.ls.TaskAppException
import java.lang.IllegalStateException
import java.lang.NumberFormatException

object UrlUtils {
    const val LIMIT_NAME = "limit"
    const val SKIP_NAME = "skip"
    const val LIMIT_DEFAULT = 25
    const val SKIP_DEFAULT = 0

    private fun Request.getQueryInt(name: String, defaultValue: Int): Int {
        val query = this.query(name)
        return if (query != null) {
            try {
                val result = query.toInt()
                if (result >= 0) result else defaultValue
            } catch (_: NumberFormatException) { defaultValue }
        } else {
            defaultValue
        }
    }

    fun getLimit(request: Request): Int = request.getQueryInt(LIMIT_NAME, LIMIT_DEFAULT)

    fun getSkip(request: Request): Int = request.getQueryInt(SKIP_NAME, SKIP_DEFAULT)

    fun getPathInt(request: Request, name: String, fragmentName: String = name): Int {
        return try {
            val result = request.path(name)?.toInt()
            checkNotNull(result) { "$fragmentName not provided in URL." }
        } catch (ex: IllegalStateException) { throw TaskAppException(ErrorCodes.URL_PATH_ERROR, issue = ex.message) } catch (ex: NumberFormatException) { throw TaskAppException(ErrorCodes.URL_PATH_TYPE_ERROR, issue = "$fragmentName is of incorrect type.") }
    }
}
