package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable
@Serializable
data class SecureOutputUserDto(val id: Int, val name: String, val email: String)
