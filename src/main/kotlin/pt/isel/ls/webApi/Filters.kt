package pt.isel.ls.webApi

import org.http4k.core.Filter
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
}
