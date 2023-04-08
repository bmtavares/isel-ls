package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable

@Serializable
data class InputBoardDto(val name: String, val description: String) : Dto
