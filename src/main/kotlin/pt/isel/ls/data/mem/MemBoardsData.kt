package pt.isel.ls.data.mem

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityAlreadyExistsException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard
import pt.isel.ls.tasksServices.dtos.EditBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardDto

object MemBoardsData : BoardsData {
    private val CASCADE_DELETE = false

    override fun getByName(name: String): Board? =
        MemDataSource.boards.firstOrNull { it.name == name }
            ?: throw EntityNotFoundException("Board not found.", Board::class)

    override fun getUserBoards(user: User): List<Board> {
        val boards = MemDataSource.usersBoards.filter { it.userId == user.id }.map { it.boardId }
        return MemDataSource.boards.filter { it.id in boards }
    }

    override fun edit(editBoard: EditBoardDto) {
        val oldBoard = MemDataSource.boards.firstOrNull { it.id == editBoard.id }
            ?: throw EntityNotFoundException("Board not found.", Board::class)
        val newBoard = Board(oldBoard.id, oldBoard.name, editBoard.description)
        MemDataSource.boards.remove(oldBoard)
        MemDataSource.boards.add(newBoard)
    }

    override fun add(inputBoardDto: InputBoardDto): Board {
        if (!MemDataSource.boards.any { it.name == inputBoardDto.name }) {
            throw EntityAlreadyExistsException(
                "Name already in use.",
                Board::class
            )
        }
        val newId = if (MemDataSource.boards.isEmpty()) 1 else MemDataSource.boards.maxOf { it.id } + 1
        val board = Board(newId, inputBoardDto.name, inputBoardDto.description)
        MemDataSource.boards.add(board)
        return board
    }

    override fun getUsers(boardId: Int, user: User): List<User> {
        val usersIds = MemDataSource.usersBoards.filter { it.boardId == boardId }.map { it.userId }
        return MemDataSource.users.filter { it.id in usersIds }
    }

    override fun addUserToBoard(user: User, board: Board) {
        val pair = UserBoard(user.id, board.id)
        if (MemDataSource.usersBoards.any { it == pair }) throw EntityAlreadyExistsException(
            "User already in board.",
            Board::class
        )
        MemDataSource.usersBoards.add(pair)
    }

    override fun getById(id: Int): Board =
        MemDataSource.boards.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "Board not found.",
            Board::class
        )

    override fun delete(id: Int) {
        val board = MemDataSource.boards.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "Board not found.",
            Board::class
        )

        if (MemDataSource.lists.any { it.boardId == id }) {
            if (CASCADE_DELETE)
                MemDataSource.lists.removeAll { it.boardId == id }
            else
                throw DataException("Cannot delete a board that has lists.")
        }

        if (MemDataSource.cards.any { it.boardId == id }) {
            if (CASCADE_DELETE)
                MemDataSource.cards.removeAll { it.listId == id }
            else
                throw DataException("Cannot delete a board that has cards.")
        }

        MemDataSource.usersBoards.removeAll { it.boardId == id }

        MemDataSource.boards.remove(board)
    }

    override fun exists(id: Int): Boolean =
        MemDataSource.boards.any { it.id == id }


}
