package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.data.pgsql.PgDataContext.handleDB

class ServiceBoards(private val boardRepository: BoardsData) {
    fun getBoard(boardId: Int, user: User): Board {
        lateinit var board: Board
        try {
            handleDB { con ->
                 board = boardRepository.getById(boardId,con)
            }
        } catch (e: Exception) {
            throw EntityNotFoundException("Board not found",Board::class)
        }
        return board
    }

    fun getBoard(boardName: String, user: User): Board {
        lateinit var board: Board
        try {
            handleDB { con ->
                board = boardRepository.getByName(boardName,con)
            }

        } catch (e: Exception) {
            throw EntityNotFoundException("Board not found",Board::class)
        }
        return board
    }

    fun getUserBoards(user: User,limit: Int = 25, skip :Int = 0): List<Board> {
        lateinit var boards: List<Board>
       try {
           handleDB { con ->
               boards = boardRepository.getUserBoards(user, limit, skip)
           }
        } catch (e: Exception) {
            throw EntityNotFoundException("Board not found",Board::class)
        }
        return boards
    }

    fun createBoard(newBoard: InputBoardDto, user: User): Board {
        lateinit var board:Board
        try {
            board = boardRepository.add(newBoard)
            boardRepository.addUserToBoard(user.id, board.id)
            return board
        } catch (e: Exception) {
            throw DataException("Failed to create Boards")
        }
    }

    fun getUsersOnBoard(boardId: Int, user: User,limit: Int = 25, skip :Int = 0): List<User> {
        lateinit var users:List<User>
         try {
            handleDB { con->
                users = boardRepository.getUsers(boardId, user,limit,skip,con)
            }
        } catch (e: Exception) {
            throw DataException("managed errors")
        }
        return users
    }

    fun addUserOnBoard(boardId: Int, userId: Int) {
        try {
            handleDB { con ->
                boardRepository.addUserToBoard(userId, boardId,con)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteUserOnBoard(boardId: Int, userId: Int) {
        try {
            handleDB { con ->
                boardRepository.deleteUserFromBoard(userId, boardId,con)
            }
        }catch (e: Exception) {
            throw e
        }
    }
}
