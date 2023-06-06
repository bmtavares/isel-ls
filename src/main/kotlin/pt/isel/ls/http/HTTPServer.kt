package pt.isel.ls.http

import org.http4k.core.Request
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

fun logRequest(request: Request) {
    logger.info(
        "incoming request: method={}, uri={}, content-type={} accept={}",
        request.method,
        request.uri,
        request.header("content-type"),
        request.header("accept")
    )
}
