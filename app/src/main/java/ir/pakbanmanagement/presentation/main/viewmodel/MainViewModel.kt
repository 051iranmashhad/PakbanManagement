package ir.pakbanmanagement.presentation.main.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.NetworkManager
import ir.pakbanmanagement.other.base.BaseViewModel
import ir.pakbanmanagement.presentation.main.fragment.HomeFragment
import ir.pakbanmanagement.presentation.main.repository.MainRepository
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class
MainViewModel @Inject constructor(
    private val mMainRepository: MainRepository,
) : BaseViewModel(mMainRepository) {

    private val _fragmentMutableStateFlow = MutableStateFlow<Fragment>(HomeFragment())
    internal val fragmentStateFlow: StateFlow<Fragment> get() = _fragmentMutableStateFlow

    internal fun setStateFragment(fragment: Fragment) = viewModelScope.launch {
        _fragmentMutableStateFlow.emit(fragment)
    }

    internal fun getUserInfo(
        supervisor: CompletableJob,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getUserInfo() }
        }
    }

    internal fun getContractTitleValue(
        supervisor: CompletableJob,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getContractTitleValue() }
        }
    }

    internal fun getSegmentTitleValueByContract(
        supervisor: CompletableJob,
        map: HashMap<String, String>,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getSegmentTitleValueByContract(map) }
        }
    }

    internal fun getEstimateDetails(
        supervisor: CompletableJob,
        map: HashMap<String, String>,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getEstimateDetails(map) }
        }
    }

    internal fun getFinancialEndPenalty(
        supervisor: CompletableJob,
        map: HashMap<String, String>,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getFinancialEndPenalty(map) }
        }
    }

    internal fun saveRequestSupervisionSpecialFine(
        supervisor: CompletableJob,
        map: HashMap<String, String>,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.saveRequestSupervisionSpecialFine(map) }
        }
    }

    internal fun getDetailByContractId(
        supervisor: CompletableJob,
        map: HashMap<String, String>,
        block: (Constant.ResultWrapper) -> Unit,
    ) {
        viewModelScope.launch(supervisor) {
            NetworkManager.httpParser(block) { mMainRepository.getDetailByContractId(map) }
        }
    }
}
