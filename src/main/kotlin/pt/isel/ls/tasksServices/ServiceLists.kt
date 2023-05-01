package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.pgsql.PgDataContext.handleDB
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

class ServiceLists(private val listsRepo: ListsData) {
    fun createBoardList(boardId: Int, newBoardList: InputBoardListDto): BoardList {
        lateinit var boardList: BoardList
        try {
            handleDB { con ->
                boardList = listsRepo.add(newBoardList, boardId, con)
            }
        } catch (e: Exception) {
            throw DataException("")
        }
        return boardList
    }

    fun getBoardLists(boardId: Int, limit: Int = 25, skip: Int = 0): List<BoardList> {
        lateinit var boardLists: List<BoardList>
        try {
            handleDB { con ->
                boardLists = listsRepo.getListsByBoard(boardId, limit, skip, con)
            }
        } catch (e: Exception) {
            throw DataException("Failed to retrieve board Lists")
        }
        return boardLists
    }

    fun getBoardList(boardId: Int, boardListId: Int): BoardList {
        lateinit var boardList: BoardList
        try {
            handleDB { con ->
                boardList = listsRepo.getById(boardListId, con)
            }
        } catch (e: Exception) {
            throw DataException("Failed to retrieve List")
        }
        return boardList
    }

    fun editBoardList(editList: EditBoardListDto, boardListId: Int, boardId: Int) {
        try {
            handleDB { con ->
                listsRepo.edit(editList.name, boardListId, boardId, con)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeList(boardId: Int, listId: Int) {
        try {
            handleDB { con ->
                listsRepo.delete(listId, con)
            }
        } catch (ex: DataException) {
            throw ex
        }
    }
}
