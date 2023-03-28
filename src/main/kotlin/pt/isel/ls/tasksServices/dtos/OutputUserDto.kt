package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable
@Serializable
data class OutputUserDto(val token:String,val id:Int):Dto