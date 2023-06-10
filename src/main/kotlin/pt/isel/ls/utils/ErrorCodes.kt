package pt.isel.ls.utils

enum class ErrorCodes(val code: Int, val message: String) {
    UNDEFINED(-1, "Undefined."),
    AUTHENTICATION_CHALLENGE_FAILED(3000, "Authentication challenge failed.")
}
