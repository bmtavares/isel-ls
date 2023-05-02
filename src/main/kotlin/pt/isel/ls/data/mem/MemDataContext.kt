package pt.isel.ls.data.mem

import pt.isel.ls.data.DataContext
import java.sql.Connection

object MemDataContext : DataContext {
    override fun handleData(block: (Connection?) -> Unit) {
        block(null)
    }
}
