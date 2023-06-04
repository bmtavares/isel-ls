package pt.isel.ls.data.mem

import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto
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

    override fun edit(editName: String, listId: Int, boardId: Int, ncards : Int, connection: Connection?) {
        val oldList = MemDataSource.lists.firstOrNull { it.id == listId && it.boardId == boardId }
            ?: throw EntityNotFoundException("List not found.", BoardList::class)
        val newList = BoardList(oldList.id, editName, oldList.boardId,ncards)
        MemDataSource.lists.remove(oldList)
        MemDataSource.lists.add(newList)
    }

    override fun add(inputListDto: InputBoardListDto, boardId: Int, connection: Connection?): BoardList {
        if (!MemDataSource.boards.any { it.id == boardId }) {
            throw EntityNotFoundException(
                "Board does not exist.",
                BoardList::class
            )
        }
        val newId = if (MemDataSource.lists.isEmpty()) 1 else MemDataSource.lists.maxOf { it.id } + 1
        val list = BoardList(newId, inputListDto.name, boardId,0)
        MemDataSource.lists.add(list)
        return list
    }

    override fun getById(id: Int, connection: Connection?): BoardList =
        MemDataSource.lists.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "List not found.",
            BoardList::class
        )

    override fun delete(id: Int, connection: Connection?) {
        val list = MemDataSource.lists.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "List not found.",
            BoardList::class
        )

        if (MemDataSource.cards.any { it.listId == id }) {
            if (CASCADE_DELETE) {
                MemDataSource.cards.removeAll { it.listId == id }
            } else {
                throw DataException("Cannot delete a list that has cards.")
            }
        }

        MemDataSource.lists.remove(list)
    }

    override fun exists(id: Int, connection: Connection?): Boolean =
        MemDataSource.lists.any { it.id == id }
}
