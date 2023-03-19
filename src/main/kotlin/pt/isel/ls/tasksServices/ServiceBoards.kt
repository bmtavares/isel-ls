package pt.isel.ls.tasksServices

import pt.isel.ls.server.Board
import pt.isel.ls.server.User
import pt.isel.ls.storage.BoardStorage

class ServiceBoards(val boardRepository: BoardStorage) {

    fun getBoard(boardId:Int,user:User):Board?{
        return try {
            val board = boardRepository.getBoardDetails(boardId,user)
            board
        }catch (e:Exception){
            null
        }
    }

    fun getUsersOnBoard(boardId:Int,user:User):List<User>?{
        return try {
            val users = boardRepository.getUsers(boardId,user)
            users
        }catch (e:Exception){
            null
        }
    }
}