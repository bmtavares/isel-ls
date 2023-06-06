package pt.isel.ls.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

object PasswordUtils {
    fun generateSalt(): String {
        val bytes = ByteArray(8)
        SecureRandom.getInstanceStrong().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hashPassword(password: String, salt: String): String {
        val saltedPassword = password + salt
        val hashedBytes = MessageDigest.getInstance("SHA-256").digest(saltedPassword.toByteArray(Charsets.UTF_8))
        // Convert to a string
        // Source: https://gist.github.com/lovubuntu/164b6b9021f5ba54cefc67f60f7a1a25
        return hashedBytes.fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }.toString()
    }
}
