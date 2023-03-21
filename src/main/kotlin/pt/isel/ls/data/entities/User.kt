package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
    override val id: Int?,
    var name: String,
    var email: String
) : Entity {
    override fun clone(id: Int): User = copy(id = id)
}
