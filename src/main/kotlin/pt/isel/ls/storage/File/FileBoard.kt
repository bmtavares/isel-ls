package pt.isel.ls.storage.File

import pt.isel.ls.server.Board
import pt.isel.ls.server.BoardList
import pt.isel.ls.server.User
import pt.isel.ls.storage.BoardStorage

class FileBoard:BoardStorage {
    override fun createBoard(name: String, description: String): Int {
        TODO("Not yet implemented")
    }

    override fun addUser(userId: Int, boardId: Int) {
        TODO("Not yet implemented")
    }

    override fun getBoardDetails(boardId: Int, user: User): Board? {
        TODO("Not yet implemented")
    }


    override fun getUsers(boardId: Int, user: User):List<User> {
        TODO("Not yet implemented")
    }

    override fun createNewList(name: String): Int {
        TODO("Not yet implemented")
    }

    override fun getLists(boardId: Int,user: User): List<BoardList> {
        TODO("Not yet implemented")
    }

    override fun getListDetails(boardId: Int, listId: Int) {
        TODO("Not yet implemented")
    }

    override fun createCard(boardId: Int, listId: Int, name: String, description: String, dueDate: String): Int {
        TODO("Not yet implemented")
    }

    override fun getCardsonList() {
        TODO("Not yet implemented")
    }
}