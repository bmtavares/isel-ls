package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard

class MemBoardsData(private val boardsList: MutableList<Board>, private val usersBoardsList: MutableList<UserBoard>) :
    MemGenericData<Board>(boardsList), BoardsData {
    override fun getByName(name: String): Board? = boardsList.find { b -> b.name == name }

    override fun getUserBoards(user: User): List<Board> {
        val boardIds = usersBoardsList
            .filter { ub -> ub.userId == user.id }
            .map { it.boardId }
        return boardsList.filter { b -> b.id in boardIds }
    }
}
