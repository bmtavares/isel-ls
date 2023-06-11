package pt.isel.ls.tasksServices.users

import pt.isel.ls.TaskAppException
import pt.isel.ls.utils.ErrorCodes

class ChallengeFailureException : TaskAppException(ErrorCodes.AUTHENTICATION_CHALLENGE_FAILED)
