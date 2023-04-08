package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable
import pt.isel.ls.data.entities.Entity

@Serializable
data class OutputEntitiesDto<T : Entity>(val list: List<T>)
