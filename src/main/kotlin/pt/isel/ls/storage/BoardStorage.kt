package pt.isel.ls.storage

import pt.isel.ls.server.Board
import pt.isel.ls.server.BoardList
import pt.isel.ls.server.User

interface BoardStorage {

    fun createBoard(name:String,description:String):Int

    fun addUser(userId:Int,boardId:Int)

    fun getBoardDetails(boardId:Int,user: User): Board?

    fun getUsers(boardId:Int,user: User):List<User>

    fun createNewList(name:String):Int

    fun getLists(boardId: Int,user: User):List<BoardList> //to be change to List<BoardList>

    fun getListDetails(boardId: Int,listId:Int)

    fun createCard(boardId: Int,listId: Int,name: String,description: String,dueDate:String):Int

    fun getCardsonList()
}