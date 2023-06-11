package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataContext
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.SecureOutputUserDto

class ServiceBoards(private val context: DataContext, private val boardRepository: BoardsData) {
    fun getBoard(boardId: Int, user: User): Board {
        lateinit var board: Board
        context.handleData { con ->
            board = boardRepository.getById(boardId, con)
        }

        return board
    }

    fun getBoard(boardName: String, user: User): Board {
        lateinit var board: Board
        context.handleData { con ->
            board = boardRepository.getByName(boardName, con)
        }

        return board
    }

    fun getUserBoards(user: User, searchField: String?, limit: Int = 25, skip: Int = 0): List<Board> {
        lateinit var boards: List<Board>
        if (searchField.isNullOrBlank()) {
            context.handleData { con ->
                boards = boardRepository.getUserBoards(user, limit, skip, con)
            }
        } else {
            context.handleData { con ->
                boards = boardRepository.filterByName(user, searchField, con)
            }
        }

        return boards
    }

    fun createBoard(newBoard: InputBoardDto, user: User): Board {
        lateinit var board: Board
        context.handleData {
            board = boardRepository.add(newBoard, it)
            boardRepository.addUserToBoard(user.id, board.id, it)
        }

        return board
    }

    fun getUsersOnBoard(boardId: Int, user: User, limit: Int = 25, skip: Int = 0): List<SecureOutputUserDto> {
        lateinit var users: List<SecureOutputUserDto>
        context.handleData { con ->
            users = boardRepository.getUsers(boardId, user, limit, skip, con)
        }

        return users
    }

    fun addUserOnBoard(boardId: Int, userId: Int) {
        context.handleData { con ->
            boardRepository.addUserToBoard(userId, boardId, con)
        }
    }

    fun deleteUserOnBoard(boardId: Int, userId: Int) {
        context.handleData { con ->
            boardRepository.deleteUserFromBoard(userId, boardId, con)
        }
    }
}
