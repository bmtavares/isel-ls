package pt.isel.ls.tasksServices
import org.http4k.lens.Invalid
import org.http4k.lens.StringBiDiMappings.uuid
import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage
import java.io.InvalidClassException
import java.io.InvalidObjectException
import java.util.*

enum class UserResponses(val code:Int,val desc:String){
    Ok(0,"Ok"),
    Created(1,"Created"),
    InvalidUser(2,"Invalid Email"),
    NotAuthorized(3,"Not Authorized"),
    ServerError(4,"Server Not Available"),
    AlreadyExist(5,"User Already registered with this email"),
    InvalidToken(6,"No user found with the current token")
}

class ServiceUsers(val userRepository: UserStorage) {
    fun createUser(u : User):UserResponses{
        if (!EmailValidator.isEmailValid(u.email)) return UserResponses.InvalidUser
        try {
            userRepository.createUser(u)
        }catch (e:Exception){
            return UserResponses.ServerError
        }


        // created
        // alredy exist
        return UserResponses.Ok


    }
    fun getUserByToken(token: String):Pair<User?,UserResponses>?{
        return try {
            val rsp = userRepository.getUserByToken(token)
            if (rsp?.first != null) rsp
            else Pair(null,UserResponses.InvalidToken)
        }catch (e:Exception){
            null
        }

    }

    class EmailValidator{
        companion object {
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";
            fun isEmailValid(email: String): Boolean {
                return EMAIL_REGEX.toRegex().matches(email);
            }
        }
    }

}