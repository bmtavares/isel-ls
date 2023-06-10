package pt.isel.ls.webApi

import org.http4k.core.Filter
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.data.DataException
import pt.isel.ls.http.logRequest
import pt.isel.ls.tasksServices.TasksServices

class Filters(
    private val services: TasksServices
) {
    val logRequest = Filter { next ->
        {
            logRequest(it)
            next(it)
        }
    }
    fun filterUser(contexts: RequestContexts) = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                    val user = services.users.getUserByToken(token)
                    contexts[request]["user"] = user
                    next(request)
                } catch (e: Exception) {
                    when (e) {
                        is DataException -> Response(Status.UNAUTHORIZED).body("Invalid token")
                        else -> Response(Status.INTERNAL_SERVER_ERROR).body("Server Error")
                    }
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }
    val authFilter = Filter { next ->
        { request ->
            val authHeader = request.header("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                    services.users.getUserByToken(token)
                    next(request)
                } catch (e: Exception) {
                    when (e) {
                        is DataException -> Response(Status.UNAUTHORIZED).body("Invalid token")
                        else -> Response(Status.INTERNAL_SERVER_ERROR).body("Server Error")
                    }
                }
            } else {
                Response(Status.UNAUTHORIZED).body("Missing or invalid Authorization header")
            }
        }
    }
}
