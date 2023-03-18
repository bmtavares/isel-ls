package pt.isel.ls.data.entities

data class User(
    override val id: Int?,
    var name: String,
    var email: String
) : Entity {
    override fun clone(id: Int): User = copy(id = id)
}
