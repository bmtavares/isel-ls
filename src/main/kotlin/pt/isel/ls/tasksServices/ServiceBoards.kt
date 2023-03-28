package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto

class ServiceBoards(val boardRepository: BoardsData) {

    fun getBoard(boardId:Int,user: User): Board?{
        return try {
            val board = boardRepository.getById(boardId)
            board
        }catch (e:Exception){
            null
        }
    }

    fun createBoard(newBoard:InputBoardDto):Board?{

        return try {
             boardRepository.add(newBoard)
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