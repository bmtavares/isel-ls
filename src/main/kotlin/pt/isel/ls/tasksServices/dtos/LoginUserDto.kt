package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserDto(val email: String, val password: String)
