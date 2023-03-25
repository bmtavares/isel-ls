package pt.isel.ls.data

class EntityFieldCheckException(override val message: String, val entityType: Any, val fieldName: String) : Exception(message)
