package pt.isel.ls.data.mem

import pt.isel.ls.TaskAppException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
import pt.isel.ls.utils.ErrorCodes
import java.sql.Connection

object MemListsData : ListsData {
    private val CASCADE_DELETE = true

    override fun getListsByBoard(boardId: Int, limit: Int, skip: Int, connection: Connection?): List<BoardList> {
        val lists = MemDataSource.lists.filter { it.boardId == boardId }

        if (skip > lists.lastIndex) return emptyList()

        return lists.subList(
            skip,
            if (skip + limit <= lists.lastIndex) skip + limit else lists.lastIndex + 1
        )
    }

    override fun edit(editName: String, listId: Int, boardId: Int, ncards: Int, connection: Connection?) {
        val oldList = MemDataSource.lists.firstOrNull { it.id == listId && it.boardId == boardId }
            ?: throw TaskAppException(ErrorCodes.LIST_UPDATE_FAIL)
        val newList = BoardList(oldList.id, editName, oldList.boardId, ncards)
        MemDataSource.lists.remove(oldList)
        MemDataSource.lists.add(newList)
    }

    override fun add(newBoardList: InputBoardListDto, boardId: Int, connection: Connection?): BoardList {
        if (!MemDataSource.boards.any { it.id == boardId }) {
            throw TaskAppException(ErrorCodes.BOARD_READ_FAIL)
        }
        val newId = if (MemDataSource.lists.isEmpty()) 1 else MemDataSource.lists.maxOf { it.id } + 1
        val list = BoardList(newId, newBoardList.name, boardId, 0)
        MemDataSource.lists.add(list)
        return list
    }

    override fun getById(id: Int, connection: Connection?): BoardList =
        MemDataSource.lists.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.LIST_READ_FAIL)

    override fun delete(id: Int, connection: Connection?) {
        val list = MemDataSource.lists.firstOrNull { it.id == id } ?: throw TaskAppException(ErrorCodes.LIST_READ_FAIL)

        if (MemDataSource.cards.any { it.listId == id }) {
            if (CASCADE_DELETE) {
                MemDataSource.cards.removeAll { it.listId == id }
            } else {
                // Should never happen due to cascade being used in live, thus doesn't have a specific code
                throw TaskAppException(message = "Cannot delete a list that has cards.")
            }
        }

        MemDataSource.lists.remove(list)
    }

    override fun exists(id: Int, connection: Connection?): Boolean =
        MemDataSource.lists.any { it.id == id }
}
