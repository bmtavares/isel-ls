package pt.isel.ls.tasksServices

import pt.isel.ls.data.BoardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.*

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
                                boardRepository.addUserToBoard(user.id,board.id)
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

    fun getBoardLists(boardId: Int)= try {
        boardRepository.boardLists.getListsByBoard(boardId)
    }catch (e:Exception){
        throw DataException("Failed to retrieve board Lists")
    }

    fun createCard(newCard: InputCardDto,boardId: Int,boardListId: Int):Card{

       return try {
            boardRepository.cards.add(newCard,boardId,boardListId)
        }catch (e:Exception){
            throw DataException("Failed to create the card")
        }
    }

    fun getCardsOnList(boardId: Int,boardListId: Int):List<Card> =try {
        boardRepository.cards.getByList(boardId,boardListId)
    }catch (e:Exception){
        throw DataException("Failed to retrieve Cards")
    }

    fun getBoardList(boardId: Int,boardListId: Int):BoardList=try {
        boardRepository.boardLists.getById(boardListId)
    }catch (e:Exception){
        throw DataException("Failed to retrieve List")
    }
    fun getCard(boardId: Int,cardId:Int):Card=try {
     boardRepository.cards.getById(cardId)
    }catch (e:Exception){
        throw DataException("Failed to retrieve List")
    }

    fun addUserOnBoard(boardId: Int,userId:Int)=try {
        boardRepository.addUserToBoard(userId,boardId)
    }catch (_:Exception){

    }
    fun deleteUserOnBoard(boardId: Int,userId:Int)=try {
        boardRepository.deleteUserFromBoard(userId,boardId)
    }catch (_:Exception){

    }

    fun editBoardList(editList:EditBoardListDto,boardListId: Int,boardId: Int){
       return try {
            boardRepository.boardLists.edit(editList.name,boardListId,boardId)
        }catch (_:Exception){

       }
    }
    fun editCard(editCard: EditCardDto, boardId: Int, cardId: Int){
        return try {
            boardRepository.cards.edit(editCard,boardId,cardId)
        }catch (_:Exception){

        }
    }
    fun moveCard(moveList:InputMoveCardDto, boardId: Int, cardId: Int){
        return try {
            boardRepository.cards.move(moveList,boardId, cardId)
        }catch (_:Exception){

        }
    }
}