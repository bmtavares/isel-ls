package pt.isel.ls.utils

import org.http4k.core.Request

object UrlUtils {
    fun getLimit(request: Request): Int {
        return try {
            if (Integer.parseInt(request.query("limit").toString()) >= 0) {
                Integer.parseInt(request.query("limit").toString())
            } else {
                25
            }
        } catch (e: java.lang.Exception) {
            25
        }
    }

    fun getSkip(request: Request): Int {
        return try {
            if (Integer.parseInt(request.query("skip").toString()) >= 0) {
                Integer.parseInt(request.query("skip").toString())
            } else {
                0
            }
        } catch (e: java.lang.Exception) { 0 }
    }
}
