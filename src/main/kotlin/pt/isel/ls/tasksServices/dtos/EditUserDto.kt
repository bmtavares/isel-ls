package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EditUserDto(val id:Int,val name:String):Dto
