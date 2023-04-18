package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto

class ServiceBoards(private val boardRepository: BoardsData) {
    fun getBoard(boardId: Int, user: User): Board? {
        return try {
            val board = boardRepository.getById(boardId)
            board
        } catch (e: Exception) {
            null
        }
    }

    fun getUserBoards(user: User,limit: Int = 25, skip :Int = 0): List<Board> {
        return try {
            boardRepository.getUserBoards(user, limit,skip)
        } catch (e: Exception) {
            throw DataException("Failed to retrieve Boards")
        }
    }

    fun createBoard(newBoard: InputBoardDto, user: User): Board? {
        return try {
            val board = boardRepository.add(newBoard)
            boardRepository.addUserToBoard(user.id, board.id)
            return board
        } catch (e: Exception) {
            null
        }
    }

    fun getUsersOnBoard(boardId: Int, user: User,limit: Int = 25, skip :Int = 0): List<User> {
        return try {
            val users = boardRepository.getUsers(boardId, user,limit,skip)
            users
        } catch (e: Exception) {
            throw DataException("managed errors")
        }
    }

    fun addUserOnBoard(boardId: Int, userId: Int) = try {
        boardRepository.addUserToBoard(userId, boardId)
    } catch (_: Exception) {
    }

    fun deleteUserOnBoard(boardId: Int, userId: Int) = try {
        boardRepository.deleteUserFromBoard(userId, boardId)
    } catch (_: Exception) {
    }
}
