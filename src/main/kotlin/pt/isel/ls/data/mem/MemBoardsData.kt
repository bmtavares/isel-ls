package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard

object MemBoardsData : MemGenericData<Board>(emptyList<Board>() as MutableList<Board>), BoardsData {
    private val boardsList = mutableListOf<Board>()
    private val usersBoardsList = mutableListOf<UserBoard>()

    init {
        // Initialization code here
    }

    override fun getByName(name: String): Board? = boardsList.find { b -> b.name == name }

    override fun getUserBoards(user: User): List<Board> {
        val boardIds = usersBoardsList
            .filter { ub -> ub.userId == user.id }
            .map { it.boardId }
        return boardsList.filter { b -> b.id in boardIds }
    }

    operator fun invoke(): BoardsData {
        return this
    }

}