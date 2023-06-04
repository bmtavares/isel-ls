package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataContext
import pt.isel.ls.data.DataException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

class ServiceLists(private val context: DataContext, private val listsRepo: ListsData) {
    fun createBoardList(boardId: Int, newBoardList: InputBoardListDto): BoardList {
        lateinit var boardList: BoardList
        try {
            context.handleData { con ->
                boardList = listsRepo.add(newBoardList, boardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return boardList
    }

    fun getBoardLists(boardId: Int, limit: Int = 25, skip: Int = 0): List<BoardList> {
        lateinit var boardLists: List<BoardList>
        try {
            context.handleData { con ->
                boardLists = listsRepo.getListsByBoard(boardId, limit, skip, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return boardLists
    }

    fun getBoardList(boardId: Int, boardListId: Int): BoardList {
        lateinit var boardList: BoardList
        try {
            context.handleData { con ->
                boardList = listsRepo.getById(boardListId, con)
            }
        } catch (e: Exception) {
            throw e
        }
        return boardList
    }

    fun deleteBoardList(boardListId: Int) {
        try {
            context.handleData { con ->
                listsRepo.delete(boardListId, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }
    fun editBoardList(editList: EditBoardListDto, boardListId: Int, boardId: Int,ncards :Int) {
        try {
            context.handleData { con ->
                listsRepo.edit(editList.name, boardListId, boardId,ncards, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeList(boardId: Int, listId: Int) {
        try {
            context.handleData { con ->
                listsRepo.delete(listId, con)
            }
        } catch (ex: DataException) {
            throw ex
        }
    }
}
