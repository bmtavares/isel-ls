package pt.isel.ls.tasksServices.dtos

data class CreateUserDto(val name: String, val email: String, val passwordHash: String, val salt: String)
