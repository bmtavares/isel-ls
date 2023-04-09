package pt.isel.ls.data.mem

import pt.isel.ls.data.DataException
import pt.isel.ls.data.EntityNotFoundException
import pt.isel.ls.data.ListsData
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.tasksServices.dtos.InputBoardListDto

object MemListsData : ListsData {
    private val CASCADE_DELETE = false

    override fun getListsByBoard(boardId: Int, limit: Int, skip: Int): List<BoardList> {
        val lists = MemDataSource.lists.filter { it.boardId == boardId }.subList(skip, skip + limit)

        if (skip > lists.lastIndex) return emptyList()

        return lists.slice(
            skip..if (skip + limit <= lists.lastIndex) skip + limit else lists.lastIndex
        )
    }

    override fun edit(editName: String, listId: Int, boardId: Int) {
        val oldList = MemDataSource.lists.firstOrNull { it.id == listId && it.boardId == boardId }
            ?: throw EntityNotFoundException("List not found.", BoardList::class)
        val newList = BoardList(oldList.id, editName, oldList.boardId)
        MemDataSource.lists.remove(oldList)
        MemDataSource.lists.add(newList)
    }

    override fun add(inputListDto: InputBoardListDto, boardId: Int): BoardList {
        if (!MemDataSource.boards.any { it.id == boardId }) {
            throw EntityNotFoundException(
                "Board does not exist.",
                BoardList::class
            )
        }
        val newId = if (MemDataSource.lists.isEmpty()) 1 else MemDataSource.lists.maxOf { it.id } + 1
        val list = BoardList(newId, inputListDto.name, boardId)
        MemDataSource.lists.add(list)
        return list
    }

    override fun getById(id: Int): BoardList =
        MemDataSource.lists.firstOrNull { it.id == id } ?: throw EntityNotFoundException(
            "List not found.",
            BoardList::class
        )

    override fun delete(id: Int) {
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

    override fun exists(id: Int): Boolean =
        MemDataSource.lists.any { it.id == id }
}
