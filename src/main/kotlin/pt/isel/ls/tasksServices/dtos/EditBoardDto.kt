package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EditBoardDto(val id: Int, val description: String) : Dto
