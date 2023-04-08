package pt.isel.ls.server

enum class HeaderTypes(val field: String) {
    CONTENT_TYPE("content-type"),
    USER("User"),
    APP_JSON("application/json"),
    TEXT_PLAIN("text/plain"),
    ACCEPT("accept")
}
