package ir.pakbanmanagement.presentation.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.pakbanmanagement.presentation.fragment.HomeFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class
MainViewModel @Inject constructor() : ViewModel() {

    private val _fragmentMutableStateFlow = MutableStateFlow<Fragment>(HomeFragment())
    internal val fragmentStateFlow: StateFlow<Fragment> get() = _fragmentMutableStateFlow

    internal fun setStateFragment(fragment: Fragment) = viewModelScope.launch {
        _fragmentMutableStateFlow.emit(fragment)
    }
}
