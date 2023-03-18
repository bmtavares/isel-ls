package pt.isel.ls.data

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User

interface BoardsData : Data<Board> {
    fun getByName(name: String): Board?
    fun getUserBoards(user: User): List<Board>
}
