package ir.pakbanmanagement.other.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.pakbanmanagement.mapper.UserMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val mBaseRepository: BaseRepository,
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    internal open fun setUser(userMapper: UserMapper) {
        viewModelScope.launch {
            mBaseRepository.setUser(userMapper)
        }
    }

    internal open fun getUser(block: (UserMapper?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            mBaseRepository.getUser(block)
        }
    }

    internal open suspend fun getUser() = mBaseRepository.getUser()

    internal open suspend fun getToken() = mBaseRepository.getToken()
}
