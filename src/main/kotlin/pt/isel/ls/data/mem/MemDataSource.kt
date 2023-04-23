package pt.isel.ls.data.mem

import pt.isel.ls.data.entities.Board
import pt.isel.ls.data.entities.BoardList
import pt.isel.ls.data.entities.Card
import pt.isel.ls.data.entities.User
import pt.isel.ls.data.entities.UserBoard
import pt.isel.ls.data.entities.UserToken
import java.sql.Timestamp
import java.util.*

object MemDataSource {
    val users = mutableListOf<User>()
    val usersTokens = mutableListOf<UserToken>()
    val boards = mutableListOf<Board>()
    val usersBoards = mutableListOf<UserBoard>()
    val lists = mutableListOf<BoardList>()
    val cards = mutableListOf<Card>()

    private fun initStorage() {
        users.addAll(
            listOf(
                User(1, "Beatriz", "beatriz@example.org"),
                User(2, "Fatima", "fatima@example.org"),
                User(3, "Miguel", "miguel@example.org")
            )
        )

        usersTokens.addAll(
            listOf(
                UserToken(UUID.fromString("f52129ca-ccf1-42cc-a363-fdc89f71901b"), 1, Timestamp.valueOf("2023-02-23 18:30:00")),
                UserToken(UUID.fromString("6d061c83-707f-4143-9c66-5128a6c5ea63"), 2, Timestamp.valueOf("2023-02-23 18:31:00")),
                UserToken(UUID.fromString("95b36fe5-a100-462c-9123-dc310f92defc"), 3, Timestamp.valueOf("2023-02-23 18:31:30"))
            )
        )

        boards.add(Board(1, "Viagem na Europa", "Reunir as ideias da viagem"))

        usersBoards.addAll(
            listOf(
                UserBoard(1, 1),
                UserBoard(2, 1),
                UserBoard(3, 1)
            )
        )

        lists.addAll(
            listOf(
                BoardList(1, "Reservas", 1),
                BoardList(2, "Sitos para comer", 1),
                BoardList(3, "Sitos para visitar", 1)
            )
        )

        cards.addAll(
            listOf(
                Card(1, "Viena", "Austria", null, null, 1),
                Card(2, "Roma", "Italia", null, null, 1),
                Card(3, "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch", "Pais de Gales", null, null, 1),
                Card(4, "Avião", "Comprar os bilhetes para todos", Timestamp.valueOf("2023-04-15 12:30:00"), 1, 1),
                Card(5, "Hotel", "Reservar os quartos (para quantos?)", Timestamp.valueOf("2023-05-01 12:00:00"), 1, 1),
                Card(6, "Transportes", "De e para o aeroporto", Timestamp.valueOf("2023-06-09 23:59:59"), 1, 1)
            )
        )
    }

    fun resetStorage() {
        clearStorage()
        initStorage()
    }

    fun clearStorage() {
        users.clear()
        usersTokens.clear()
        boards.clear()
        usersBoards.clear()
        lists.clear()
        cards.clear()
    }
}
