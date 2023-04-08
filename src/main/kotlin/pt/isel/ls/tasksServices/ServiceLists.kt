package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

class ServiceLists(private val listsRepo: ListsData) {
    fun createBoardList(boardId: Int, newBoardList: InputBoardListDto): BoardList {
        return try {
            listsRepo.add(newBoardList, boardId)
        } catch (e: Exception) {
            throw DataException("")
        }
    }

    fun getBoardLists(boardId: Int) = try {
        listsRepo.getListsByBoard(boardId)
    } catch (e: Exception) {
        throw DataException("Failed to retrieve board Lists")
    }

    fun getBoardList(boardId: Int, boardListId: Int): BoardList = try {
        listsRepo.getById(boardListId)
    } catch (e: Exception) {
        throw DataException("Failed to retrieve List")
    }

    fun editBoardList(editList: EditBoardListDto, boardListId: Int, boardId: Int) {
        return try {
            listsRepo.edit(editList.name, boardListId, boardId)
        } catch (_: Exception) {
        }
    }
}
