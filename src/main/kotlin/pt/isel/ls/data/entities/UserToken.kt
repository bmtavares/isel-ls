package pt.isel.ls.data.entities

import java.sql.Timestamp
import java.util.UUID

data class UserToken(
    val token: UUID,
    val userId: Int,
    val creationDate: Timestamp
)
