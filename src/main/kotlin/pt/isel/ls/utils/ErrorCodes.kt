package pt.isel.ls.utils

import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED

enum class ErrorCodes(val code: Int, val message: String) {
    UNDEFINED(500, "An error has occurred, please try again later."),
    URL_PATH_ERROR(600, "There was an issue with one of the given parameters."),
    URL_QUERY_ERROR(601, "There was an issue with one of the given query parameters."),
    JSON_BODY_ERROR(602, "There was an issue with the supplied body."),
    URL_PATH_TYPE_ERROR(603, "There was an issue with the type of one of the given parameters."),
    PGSQL_CONN_NULL(700, "An error has occurred, please try again later."),
    BOARD_READ_FAIL(1000, "Board not found."),
    BOARD_CREATE_FAIL(1001, "Board could not be created."),
    BOARD_DELETE_FAIL(1002, "Board could not be deleted."),
    BOARD_UPDATE_FAIL(1003, "Board could not be updated."),
    LIST_READ_FAIL(2000, "List not found."),
    LIST_CREATE_FAIL(2001, "List could not be created."),
    LIST_DELETE_FAIL(2002, "List could not be deleted."),
    LIST_UPDATE_FAIL(2003, "List could not be updated."),
    AUTHENTICATION_CHALLENGE_FAILED(3000, "Authentication challenge failed."),
    NO_EMAIL_MATCH(3001, "Invalid email."),
    NO_TOKEN_MATCH(3002, "Invalid token."),
    EMAIL_ALREADY_IN_USE(3003, "Specified email is already in use."),
    EMAIL_FAILED_CHECK(3004, "Specified email format is incorrect."),
    NOT_AUTHENTICATED(3005, "Client is not correctly authenticated."),
    USER_READ_FAIL(3006, "User not found."),
    USER_CREATE_FAIL(3007, "User could not be created."),
    USER_DELETE_FAIL(3008, "User could not be deleted."),
    USER_UPDATE_FAIL(3009, "User edit failed."),
    TOKEN_GENERATION_FAILED(3010, "Token could not be generated at this moment."),
    AUTH_HEADER_MISSING(3011, "Missing or invalid Authorization header."),
    CARD_READ_FAIL(4000, "Card not found."),
    CARD_CREATE_FAIL(4001, "Card could not be created."),
    CARD_DELETE_FAIL(4002, "Card could not be deleted."),
    CARD_MOVE_FAIL(4003, "Card could not be moved.");

    fun http4kStatus(): Status =
        when (this) {
            URL_PATH_ERROR, URL_QUERY_ERROR, JSON_BODY_ERROR, EMAIL_FAILED_CHECK, URL_PATH_TYPE_ERROR -> BAD_REQUEST
            BOARD_READ_FAIL, LIST_READ_FAIL, USER_READ_FAIL, CARD_READ_FAIL -> NOT_FOUND
            EMAIL_ALREADY_IN_USE -> CONFLICT
            AUTHENTICATION_CHALLENGE_FAILED, NO_EMAIL_MATCH, NO_TOKEN_MATCH, NOT_AUTHENTICATED, AUTH_HEADER_MISSING -> UNAUTHORIZED
            else -> INTERNAL_SERVER_ERROR
        }
}
