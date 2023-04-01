package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.InputBoardDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

class ServiceBoards(val boardRepository: BoardsData) {

    fun getBoard(boardId:Int,user: User): Board?{
        return try {
            val board = boardRepository.getById(boardId)
            board
        }catch (e:Exception){
            null
        }
    }
    fun getUserBoards(user: User):List<Board>{
        return try {
            boardRepository.getUserBoards(user)
        }catch (e:Exception){
           throw DataException("Failed to retrieve Boards")
        }
    }
    fun createBoard(newBoard:InputBoardDto,user: User):Board?{
        return try {
             val board = boardRepository.add(newBoard)
                                boardRepository.addUserToBoard(user,board)
                    return board
        }catch (e:Exception){
            null
        }
    }

    fun getUsersOnBoard(boardId:Int,user:User):List<User>{
        return try {
           val users = boardRepository.getUsers(boardId,user)
           users
        }catch (e:Exception){
            throw DataException("managed errors")
        }
    }

    fun createBoardList(boardId:Int,newBoardList: InputBoardListDto):BoardList{
        return try {
           boardRepository.boardLists.add(newBoardList,boardId)
        }catch (e:Exception){
            throw DataException("")
        }
    }
}