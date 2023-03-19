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
@Serializable
data class User(val id:Int,val name:String,val email:String)

@Serializable
data class NewUser(val name:String,val email:String)

@Serializable
data class Board(
    val id:Int,
    val name:String,
    val description:String
    )

@Serializable
data class NewBoard(val name: String,val description: String)

@Serializable
data class BoardList(val id: Int,val name: String,val boardID:Int)



@Serializable
data class Card(val id:Int,
                val name:String,
                val description: String,
                val dueDate: LocalDateTime,
                val listId:Int?,
                val boardId:Int
                )