package pt.isel.ls.tasksServices.users

import org.http4k.core.Status
import pt.isel.ls.TaskAppException
import pt.isel.ls.utils.ErrorCodes

class ChallengeFailureException : TaskAppException(ErrorCodes.AUTHENTICATION_CHALLENGE_FAILED, Status.UNAUTHORIZED)
