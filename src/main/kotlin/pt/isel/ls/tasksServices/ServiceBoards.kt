package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataContext
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto

class ServiceBoards(private val context: DataContext, private val boardRepository: BoardsData) {
    fun getBoard(boardId: Int, user: User): Board {
        lateinit var board: Board
        try {
            context.handleData { con ->
                board = boardRepository.getById(boardId, con)
            }
        } catch (e: Exception) {
            throw EntityNotFoundException("Board not found", Board::class)
        }
        return board
    }

    fun getBoard(boardName: String, user: User): Board {
        lateinit var board: Board
        try {
            context.handleData { con ->
                board = boardRepository.getByName(boardName, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return board
    }

    fun getUserBoards(user: User, searchField: String?, limit: Int = 25, skip: Int = 0): List<Board> {
        lateinit var boards: List<Board>
        if (searchField.isNullOrBlank()) {
            try {
                context.handleData { con ->
                    boards = boardRepository.getUserBoards(user, limit, skip, con)
                }
            } catch (e: Exception) {
                throw EntityNotFoundException("Board not found", Board::class)
            }
        } else {
            try {
                context.handleData { con ->
                    boards = boardRepository.filterByName(user, searchField, con)
                }
            } catch (e: Exception) {
                throw EntityNotFoundException("Board not found", Board::class)
            }
        }
        return boards
    }

    fun createBoard(newBoard: InputBoardDto, user: User): Board {
        lateinit var board: Board
        try {
            context.handleData {
                board = boardRepository.add(newBoard, it)
                boardRepository.addUserToBoard(user.id, board.id, it)
            }
        } catch (e: Exception) {
            throw e
        }
        return board
    }

    fun getUsersOnBoard(boardId: Int, user: User, limit: Int = 25, skip: Int = 0): List<User> {
        lateinit var users: List<User>
        try {
            context.handleData { con ->
                users = boardRepository.getUsers(boardId, user, limit, skip, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return users
    }

    fun addUserOnBoard(boardId: Int, userId: Int) {
        try {
            context.handleData { con ->
                boardRepository.addUserToBoard(userId, boardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteUserOnBoard(boardId: Int, userId: Int) {
        try {
            context.handleData { con ->
                boardRepository.deleteUserFromBoard(userId, boardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
