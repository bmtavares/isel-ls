package pt.isel.ls.data.mem

import pt.isel.ls.TaskAppException
import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.SecureOutputUserDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection

object MemBoardsData : BoardsData {
    private val CASCADE_DELETE = false
    override fun getByName(name: String, connection: Connection?): Board =
        MemDataSource.boards.firstOrNull { it.name == name }
            ?: throw TaskAppException(ErrorCodes.BOARD_READ_FAIL)

    override fun getUserBoards(user: User, limit: Int, skip: Int, connection: Connection?): List<Board> {
        val boards = MemDataSource.usersBoards.filter { it.userId == user.id }

        if (skip > boards.lastIndex) return emptyList()

        val boardIds = boards.subList(
            skip,
            if (skip + limit <= boards.lastIndex) skip + limit else boards.lastIndex + 1
        ).map { it.boardId }

        return MemDataSource.boards.filter { it.id in boardIds }
    }

    override fun edit(editBoard: EditBoardDto, connection: Connection?) {
        val oldBoard = MemDataSource.boards.firstOrNull { it.id == editBoard.id }
            ?: throw TaskAppException(ErrorCodes.BOARD_UPDATE_FAIL)
        val newBoard = Board(oldBoard.id, oldBoard.name, editBoard.description)
        MemDataSource.boards.remove(oldBoard)
        MemDataSource.boards.add(newBoard)
    }

    override fun add(newBoard: InputBoardDto, connection: Connection?): Board {
        if (MemDataSource.boards.any { it.name == newBoard.name }) {
            throw TaskAppException(ErrorCodes.BOARD_NAME_IN_USE)
        }
        val newId = if (MemDataSource.boards.isEmpty()) 1 else MemDataSource.boards.maxOf { it.id } + 1
        val board = Board(newId, newBoard.name, newBoard.description)
        MemDataSource.boards.add(board)
        return board
    }

    override fun getUsers(boardId: Int, user: User, limit: Int, skip: Int, connection: Connection?): List<SecureOutputUserDto> {
        val users = MemDataSource.usersBoards.filter { it.boardId == boardId }

        if (skip > users.lastIndex) return emptyList()

        val usersIds = users.subList(
            skip,
            if (skip + limit <= users.lastIndex) skip + limit else users.lastIndex + 1
        ).map { it.userId }

        return MemDataSource.users.filter { it.id in usersIds }.map { SecureOutputUserDto(it.id, it.name, it.email) }
    }

    override fun filterByName(user: User, searchField: String, con: Connection?): List<Board> {
        val boards = MemDataSource.usersBoards.filter { it.userId == user.id }

        val boardIds = boards.map { it.boardId }

        return MemDataSource.boards.filter { it.id in boardIds && it.name.contains(searchField.lowercase()) }
    }

    override fun addUserToBoard(userId: Int, boardId: Int, connection: Connection?) {
        val pair = UserBoard(userId, boardId)
        if (MemDataSource.boards.any { it.id == boardId } && MemDataSource.users.any { it.id == userId }) {
            if (!MemDataSource.usersBoards.any { it == pair }) {
                MemDataSource.usersBoards.add(pair)
            }
        } else {
            throw TaskAppException(ErrorCodes.ADD_USER_FAIL)
        }
    }

    override fun deleteUserFromBoard(userId: Int, boardId: Int, connection: Connection?) {
        val pair = UserBoard(userId, boardId)
        MemDataSource.usersBoards.remove(pair)
    }

    override fun getById(id: Int, connection: Connection?): Board =
        MemDataSource.boards.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.BOARD_READ_FAIL)

    override fun delete(id: Int, connection: Connection?) {
        val board = MemDataSource.boards.firstOrNull { it.id == id } ?: TaskAppException(ErrorCodes.BOARD_DELETE_FAIL)

        if (MemDataSource.lists.any { it.boardId == id }) {
            if (CASCADE_DELETE) {
                MemDataSource.lists.removeAll { it.boardId == id }
            } else {
                // Should never happen due to cascade being used in live, thus doesn't have a specific code
                throw TaskAppException(message = "Cannot delete a board that has lists.")
            }
        }

        if (MemDataSource.cards.any { it.boardId == id }) {
            if (CASCADE_DELETE) {
                MemDataSource.cards.removeAll { it.listId == id }
            } else {
                // Should never happen due to cascade being used in live, thus doesn't have a specific code
                throw TaskAppException(message = "Cannot delete a board that has cards.")
            }
        }

        MemDataSource.usersBoards.removeAll { it.boardId == id }

        MemDataSource.boards.remove(board)
    }

    override fun exists(id: Int, connection: Connection?): Boolean =
        MemDataSource.boards.any { it.id == id }
}
