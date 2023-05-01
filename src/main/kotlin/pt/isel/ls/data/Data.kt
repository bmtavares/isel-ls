package pt.isel.ls.data

import pt.isel.ls.data.entities.Entity
import java.sql.Connection

interface Data<K : Entity> {
    fun getById(id: Int,connection : Connection?=null): K
    fun delete(id: Int,connection : Connection?=null)
    fun exists(id: Int,connection : Connection?=null): Boolean
}
