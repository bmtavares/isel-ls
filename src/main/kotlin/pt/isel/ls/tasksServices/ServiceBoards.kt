package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.User

class ServiceBoards(val boardRepository: BoardsData) {

    fun getBoard(boardId:Int,user: User): Board?{
        return try {
            val board = boardRepository.getById(boardId)
            board
        }catch (e:Exception){
            null
        }
    }

    fun createBoard(name:String,description:String):Board?{

        return try {
             boardRepository.add( Board(null,name,description) )
        }catch (e:Exception){
            null
        }
    }

    fun getUsersOnBoard(boardId:Int,user:User):List<User>?{
        return try {
          //  val users = boardRepository.getUsers(boardId,user) // TODO
          //  users
            emptyList<User>()
        }catch (e:Exception){
            null
        }
    }
}