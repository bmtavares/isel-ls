package pt.isel.ls.data

class EntityNotFoundException(override val message: String, val entityType: Any) : Exception(message)
