package ir.pakbanmanagement.other.base

import ir.pakbanmanagement.mapper.UserMapper
import ir.pakbanmanagement.other.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class BaseRepository @Inject constructor() {

    open suspend fun setUser(user: UserMapper?) {
        withContext(Dispatchers.IO) {
            PrefManager.setUser(user)
        }
    }

    open suspend fun getUser() = PrefManager.getUser

    open suspend fun getUser(block: (UserMapper?) -> Unit) {
        withContext(Dispatchers.IO) {
            val userMapper = PrefManager.getUser.first()
            withContext(Dispatchers.Main) {
                block.invoke(userMapper)
            }
        }
    }

    open suspend fun getToekn() = PrefManager.getToken.first().token
}
