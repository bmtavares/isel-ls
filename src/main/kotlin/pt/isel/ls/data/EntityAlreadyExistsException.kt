package pt.isel.ls.data

class EntityAlreadyExistsException(override val message: String, val entityType: Any) : Exception(message)
