package pt.isel.ls.data.pgsql

import pt.isel.ls.data.CardsData
import pt.isel.ls.data.DataException
import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.Card
import pt.isel.ls.tasksServices.dtos.EditCardDto
import pt.isel.ls.tasksServices.dtos.InputCardDto
import pt.isel.ls.tasksServices.dtos.InputMoveCardDto
import java.sql.Connection
import java.sql.Types

object PgSqlCardsData : CardsData {

    override fun getByList(boardId: Int, listId: Int, limit: Int, skip: Int, connection: Connection?): List<Card> {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select * from Cards where listId = ? and boardid = ? offset ? limit ?;"
        )
        statement.setInt(1, listId)
        statement.setInt(2, boardId)
        statement.setInt(3, skip)
        statement.setInt(4, limit)

        val rs = statement.executeQuery()

        val results = mutableListOf<Card>()

        while (rs.next()) {
            results += Card(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("dueDate"),
                if (rs.getInt("listId") == 0 && rs.wasNull()) null else rs.getInt("listId"),
                rs.getInt("boardId"),
                rs.getInt("cidx"),
//                rs.getInt("cIdx")
            )
        }

        return results
    }

    override fun getByBoard(board: Board, connection: Connection?): List<Card> {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select * from Cards where boardId = ?;"
        )
        statement.setInt(1, board.id)

        val rs = statement.executeQuery()

        val results = mutableListOf<Card>()

        while (rs.next()) {
            results += Card(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("dueDate"),
                if (rs.getInt("listId") == 0 && rs.wasNull()) null else rs.getInt("listId"),
                rs.getInt("boardId"),
                rs.getInt("cIdx")
            )
        }

        return results
    }
    internal fun getListCount(lid: Int?, boardId: Int, connection: Connection): Int {
        val statement = connection.prepareStatement("select nCards from lists where id = ? and boardid = ?;")
        if (lid != null) {
            statement.setInt(1, lid)
        } else {
            statement.setNull(1, Types.INTEGER)
        }
        statement.setInt(2, boardId)
        val rs = statement.executeQuery()
        while (rs.next()) {
            return rs.getInt("nCards")
        }
        throw DataException("something went wrong")
    }
    internal fun getCardInfo(cardId: Int, connection: Connection): Card {
        val statement = connection.prepareStatement("select * from cards where id = ?;")
        statement.setInt(1, cardId)
        val rs = statement.executeQuery()
        while (rs.next()) {
            return Card(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("dueDate"),
                if (rs.getInt("listId") == 0 && rs.wasNull()) null else rs.getInt("listId"),
                rs.getInt("boardId"),
                rs.getInt("cidx")
            )
        }

        throw Exception("awdwa") // TODO
    }
    private fun updateCardLid(cardId: Int, lid: Int, bid: Int, connection: Connection) {
        val statement = connection.prepareStatement(
            "update Cards set listid = ? where id = ? and boardid = ?;"
        )
        /*
        * update whatever set pos = pos + 1 where pos between 2 and 3;
        * update whatever set pos = 2 where id = 4;*/
        statement.setInt(1, lid)
        statement.setInt(2, cardId)
        statement.setInt(3, bid)

        val count = statement.executeUpdate()

        if (count == 0) {
            throw DataException("Failed to edit card.")
        }
    }
    private fun updateCardIdx(cardId: Int, cIdx: Int, bid: Int, connection: Connection) {
        val statement = connection.prepareStatement(
            "update Cards set cIdx = ? where id = ? and boardid = ?;"
        )
        /*
        * update whatever set pos = pos + 1 where pos between 2 and 3;
        * update whatever set pos = 2 where id = 4;*/
        statement.setInt(1, cIdx)
        statement.setInt(2, cardId)
        statement.setInt(3, bid)
        val count = statement.executeUpdate()
        if (count == 0) {
            throw DataException("Failed to edit card.")
        }
    }

    private fun updateList(updatedCards:List<Card>, connection: Connection){
        val sql = "UPDATE Cards SET cIdx = ?, listid = ? WHERE id = ?"

        try {
            val statement = connection.prepareStatement(sql)

            for (card in updatedCards) {
                statement.setInt(1, card.cIdx)
                if (card.listId != null) {
                    statement.setInt(2, card.listId)
                } else {
                    statement.setNull(2, java.sql.Types.INTEGER)
                }
                statement.setInt(3, card.id)
                statement.executeUpdate()
            }

            statement.close()
        } catch (e: Exception) {
            throw DataException(""+e.message)
        }

    }

    override fun move(inputList: InputMoveCardDto, boardId: Int, cardId: Int, connection: Connection?) {
        checkNotNull(connection)
        val cardInfo = getCardInfo(cardId, connection)
        checkNotNull(cardInfo)
        if (inputList.cix <0) throw DataException("new position cant be negative")
        val cardOldPosition = cardInfo.cIdx
        val newPosition = inputList.cix

        val startIndex = minOf(cardOldPosition, newPosition)
        val endIndex = maxOf(cardOldPosition, newPosition)

        if (inputList.lid == cardInfo.listId) {
            if (newPosition  == cardOldPosition) return
            val  oldListOfCards  = getByList(boardId,cardInfo.listId,connection= connection)
            val updatedCards = oldListOfCards.toMutableList()
            updatedCards[updatedCards.indexOf(cardInfo)] = cardInfo.copy(cIdx = newPosition)

            val cardWithSamePosition = oldListOfCards.find { it.cIdx == newPosition }
            if (cardWithSamePosition != null) {
                if(newPosition  > cardOldPosition){
                    updatedCards[updatedCards.indexOf(cardWithSamePosition)] = cardWithSamePosition.copy(cIdx = newPosition-1)
                }else{
                    updatedCards[updatedCards.indexOf(cardWithSamePosition)] = cardWithSamePosition.copy(cIdx = newPosition+1)
                }
            }
            updatedCards.sortBy{it.cIdx}

            for (i in startIndex..endIndex) {
                val obj = updatedCards[i]
                updatedCards[i] = obj.copy(cIdx = i)
            }
            updateList(updatedCards,connection)

        } else {
            if(cardInfo.listId != null){
                // delete(cardInfo.id,connection)

                val  updatedCards  = getByList(boardId,cardInfo.listId,connection= connection).toMutableList()
                updatedCards.remove(cardInfo)
                updatedCards.sortBy{it.cIdx}
                if ((updatedCards.size != 0) and (newPosition != updatedCards.size-1) ){
                    for (i in startIndex..endIndex) {
                        val obj = updatedCards[i]
                        updatedCards[i] = obj.copy(cIdx = i)
                    }
                    updateList(updatedCards,connection)
                }

            }
            val  oldListOfCardsNewList  = getByList(boardId,inputList.lid,connection= connection)
            val updatedCardsNewList = oldListOfCardsNewList.toMutableList()


            val cardWithSamePosition = oldListOfCardsNewList.find { it.cIdx == newPosition }

            if(newPosition<=updatedCardsNewList.size){
                updatedCardsNewList.add(cardInfo.copy(listId = inputList.lid, cIdx = newPosition ))
                if (cardWithSamePosition != null) {
                    updatedCardsNewList[updatedCardsNewList.indexOf(cardWithSamePosition)] = cardWithSamePosition.copy(cIdx = newPosition+1)
                }

                updatedCardsNewList.sortBy{it.cIdx}
                if ((updatedCardsNewList.size != 0) and (newPosition != updatedCardsNewList.size-1) ) {
                    for (i in newPosition until updatedCardsNewList.size) {
                        val obj = updatedCardsNewList[i]
                        updatedCardsNewList[i] = obj.copy(cIdx = i)
                    }
                }
            }else{
                updatedCardsNewList.add(cardInfo.copy(listId = inputList.lid, cIdx = newPosition ))
            }

            updateList(updatedCardsNewList,connection)

        }


    }




    override fun getById(id: Int, connection: Connection?): Card {
        checkNotNull(connection) { "Connection is need to use DB" }
        return try {
            getCardInfo(id, connection)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override fun add(newCard: InputCardDto, boardId: Int, listId: Int?, connection: Connection?): Card {
        checkNotNull(connection) { "Connection is need to use DB" }
        val listCount = getListCount(listId, boardId, connection)
        val statement = connection.prepareStatement(
            "insert into Cards (name, description, dueDate, listId, boardId) values (?, ?, ?, ?, ?) returning id, name, description, dueDate, listId, boardId;"
        )
        statement.setString(1, newCard.name)
        statement.setString(2, newCard.description)
        if (newCard.dueDate == null) {
            statement.setNull(3, Types.TIMESTAMP)
        } else {
            statement.setTimestamp(3, newCard.dueDate)
        }
        if (listId != null) {
            statement.setInt(4, listId)
        } else {
            statement.setNull(4, Types.INTEGER)
        }

        statement.setInt(5, boardId)

        val rs = statement.executeQuery()

        while (rs.next()) {
            val id = rs.getInt("id")
            val name = rs.getString("name")
            val description = rs.getString("description")
            val dueDate = rs.getTimestamp("dueDate")
            val lId = if (rs.getInt("listId") == 0 && rs.wasNull()) null else rs.getInt("listId")
            val bId = rs.getInt("boardId")
            val cIdx = rs.getInt("cIdx")

            return Card(
                id,
                name,
                description,
                dueDate,
                lId,
                bId,
                cIdx
            )
        }
        throw DataException("Failed to add card.")
    }

    override fun delete(id: Int, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "delete from Cards where id = ?;"
        )
        statement.setInt(1, id)

        val count = statement.executeUpdate()

        if (count == 0) {
            throw DataException("Failed to delete card.")
        }
    }

    override fun edit(editCardDto: EditCardDto, boardId: Int, cardId: Int, connection: Connection?) {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "update Cards set name = ? where id = ?;" +
                "update Cards set description = ? where id = ?;" +
                "update Cards set dueDate = ? where id = ?;"

        )
        statement.setString(1, editCardDto.name)
        statement.setString(3, editCardDto.description)
        if (editCardDto.dueDate == null) {
            statement.setNull(5, Types.TIMESTAMP)
        } else {
            statement.setTimestamp(5, editCardDto.dueDate)
        }
        statement.setInt(2, cardId)
        statement.setInt(4, cardId)
        statement.setInt(6, cardId)
        statement.setInt(8, cardId)

        val count = statement.executeUpdate()

        if (count == 0) {
            throw DataException("Failed to edit card.")
        }
    }

    override fun exists(id: Int, connection: Connection?): Boolean {
        checkNotNull(connection) { "Connection is need to use DB" }
        val statement = connection.prepareStatement(
            "select count(*) exists from Cards where id = ?;"
        )
        statement.setInt(1, id)

        val rs = statement.executeQuery()
        while (rs.next()) {
            return rs.getInt("exists") == 1
        }

        return false
    }
}
