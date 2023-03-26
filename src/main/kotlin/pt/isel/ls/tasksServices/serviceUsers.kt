package pt.isel.ls.tasksServices
import pt.isel.ls.data.DataException
import pt.isel.ls.data.UsersData
import pt.isel.ls.data.entities.User
import pt.isel.ls.tasksServices.dtos.OutputUserDto

class ServiceUsers(val userRepository: UsersData) {
    fun createUser(email: String,name:String):OutputUserDto{
        if (!EmailValidator.isEmailValid(email)) throw DataException("Invalid Email")
        return try {
           // userRepository.add()
            return OutputUserDto("",1)
        } catch (e:Exception){
            throw DataException("")
        }
    }

    fun getUser(userId:Int): User{
        return try {
             userRepository.getById(userId)
        } catch (e: Exception) {
            throw DataException("")
        }
    }
    fun getUserByToken(token: String):User{
        return try {
            userRepository.getByToken(token)
        }catch (e:Exception){
           throw DataException("")
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