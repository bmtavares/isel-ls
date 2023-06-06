package pt.isel.ls.tasksServices.dtos

data class UserCredentialsDto(
    val passwordHash: String,
    val salt: String
)
