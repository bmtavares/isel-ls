package pt.isel.ls.tasksServices
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.server.User
import pt.isel.ls.storage.UserStorage

enum class UserResponses(val code:Int,var desc:String){
    Ok(0,"Ok"),
    Created(1,"Created"),
    InvalidUser(2,"Invalid Email"),
    NotAuthorized(3,"Not Authorized"),
    ServerError(4,"Server Not Available"),
    AlreadyExist(5,"User Already registered with this email"),
    InvalidToken(6,"No user found with the current token")
}

class ServiceUsers(private val usersRepo : UsersData) {
    fun createUser(email: String,name:String):UserResponses{
        if (!EmailValidator.isEmailValid(email)) return UserResponses.InvalidUser
        try {
           val rsp = usersRepo.add(User(null,name,email))
            if (rsp.second != null){
                val rsp2 = UserResponses.Created
                rsp.second.let {
                    if (it != null) {
                        rsp2.desc = it
                    }
                }
                return rsp2
            }
        }catch (e:Exception){
            return UserResponses.ServerError
        }


        // created
        // alredy exist
        return UserResponses.Ok


    }

    fun getUser(userId:Int): Pair<User?,UserResponses>?{
        return try {
            return usersRepo.getById(userId)
        } catch (e: Exception) {
            null
        }
    }
    fun getUserByToken(token: String):Pair<User?,UserResponses>?{
        return try {
            val rsp = usersRepo.getByToken(token)
            if (rsp?.first != null) rsp
            else Pair(null,UserResponses.InvalidToken)
        }catch (e:Exception){
            null
        }

    }

    class EmailValidator{
        companion object {
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            fun isEmailValid(email: String): Boolean {
                return EMAIL_REGEX.toRegex().matches(email)
            }
        }
    }

}