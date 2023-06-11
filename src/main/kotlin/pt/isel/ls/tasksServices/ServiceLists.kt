package pt.isel.ls.tasksServices

import pt.isel.ls.data.DataContext
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.EditBoardListDto
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

class ServiceLists(private val context: DataContext, private val listsRepo: ListsData) {
    fun createBoardList(boardId: Int, newBoardList: InputBoardListDto): BoardList {
        lateinit var boardList: BoardList
        context.handleData { con ->
            boardList = listsRepo.add(newBoardList, boardId, con)
        }

        return boardList
    }

    fun getBoardLists(boardId: Int, limit: Int = 25, skip: Int = 0): List<BoardList> {
        lateinit var boardLists: List<BoardList>
        context.handleData { con ->
            boardLists = listsRepo.getListsByBoard(boardId, limit, skip, con)
        }

        return boardLists
    }

    fun getBoardList(boardId: Int, boardListId: Int): BoardList {
        lateinit var boardList: BoardList
        context.handleData { con ->
            boardList = listsRepo.getById(boardListId, con)
        }

        return boardList
    }

    fun editBoardList(editList: EditBoardListDto, boardListId: Int, boardId: Int, ncards: Int) {
        context.handleData { con ->
            listsRepo.edit(editList.name, boardListId, boardId, ncards, con)
        }
    }

    fun removeList(boardId: Int, listId: Int) {
        context.handleData { con ->
            listsRepo.delete(listId, con)
        }
    }
}
