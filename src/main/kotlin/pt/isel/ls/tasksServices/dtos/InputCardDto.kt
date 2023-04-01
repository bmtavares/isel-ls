package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable

@Serializable
data class InputCardDto(val name:String,val description:String,val dueDate:String?=null):Dto
