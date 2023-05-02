package pt.isel.ls.data

import java.sql.Connection

interface DataContext {
    fun handleData(block: (Connection?) -> Unit)
}
