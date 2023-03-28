package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class BoardList(
    override val id: Int,
    var name: String,
    val boardId: Int
) : Entity {
    override fun clone(id: Int): BoardList = this.copy(id = id)
}
