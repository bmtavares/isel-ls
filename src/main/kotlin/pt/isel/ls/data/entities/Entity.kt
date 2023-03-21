package pt.isel.ls.data.entities

interface Entity {
    val id: Int?
    fun clone(id: Int): Entity
}
