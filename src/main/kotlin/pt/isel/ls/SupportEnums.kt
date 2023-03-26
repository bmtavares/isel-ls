package pt.isel.ls.server

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import java.security.Timestamp
import java.util.Date
import java.util.UUID

enum class HeaderTypes(val field:String){
    ContentType("content-type"),
    user("User"),
    AppJson("application/json"),
    TextPlain("text/plain"),
    Accept("accept"),
}