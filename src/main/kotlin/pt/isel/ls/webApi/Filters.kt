package pt.isel.ls.webApi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.ContentType
import org.http4k.core.Filter
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.slf4j.LoggerFactory
import pt.isel.ls.TaskAppException
import pt.isel.ls.server.HeaderTypes
import pt.isel.ls.tasksServices.TasksServices
import pt.isel.ls.tasksServices.dtos.ErrorDto
import pt.isel.ls.utils.ErrorCodes.AUTH_HEADER_MISSING

class Filters(
    private val services: TasksServices
) {
    private val logger = LoggerFactory.getLogger("pt.isel.ls.TaskServer")

    private fun logRequest(request: Request) {
        logger.info(
            "incoming request: method={}, uri={}, content-type={} accept={}",
            request.method,
            request.uri,
            request.header("content-type"),
            request.header("accept")
        )
    }

    val logRequest = Filter { next ->
        {
            logRequest(it)
            try {
                next(it)
            } catch (ex: TaskAppException) {
                if (ex.message != null) logger.error(ex.message)

                Response(ex.errorCode.http4kStatus())
                    .header(HeaderTypes.CONTENT_TYPE.field, ContentType.APPLICATION_JSON.value)
                    .body(
                        Json.encodeToString(
                            ErrorDto(
                                ex.issue ?: ex.errorCode.message,
                                ex.errorCode.code
                            )
                        )
                    )
            }
        }
    }

    fun filterUser(contexts: RequestContexts) = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                val user = services.users.getUserByToken(token)
                contexts[request]["user"] = user
                next(request)
            } else {
                throw TaskAppException(AUTH_HEADER_MISSING)
            }
        }
    }

    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                services.users.getUserByToken(token)
                next(request)
            } else {
                throw TaskAppException(AUTH_HEADER_MISSING)
            }
        }
    }
}
