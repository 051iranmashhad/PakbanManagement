package ir.pakbanmanagement.presentation.main.repository

import ir.pakbanmanagement.BuildConfig
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.NetworkManager
import ir.pakbanmanagement.other.base.BaseRepository
import ir.pakbanmanagement.other.fromMapper
import ir.pakbanmanagement.other.logV
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor() : BaseRepository() {

    internal suspend fun getUserInfo(): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetUserInfo".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun getContractTitleValue(): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetContractTitleValue".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun getSegmentTitleValueByContract(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetSegmentTitleValueByContract?contractId=${map[Constant.Key.ID]}".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun getEstimateDetails(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetEstimateDetails?contractId=${map[Constant.Key.ID]}".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun getFinancialEndPenalty(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetFinancialEndPenalty?contractId=${map[Constant.Key.ID]}".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun saveRequestSupervisionSpecialFine(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/SaveRequestSupervisionSpecialFine".also { it.logV() },
            method = NetworkManager.Method.POST,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            },
            body = map[Constant.Key.BODY]
        )
    }

    internal suspend fun getDetailByContractId(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetDetailByContractId?contractId=${map[Constant.Key.ID]}&requestDate=${map[Constant.Key.DATE]}".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToken()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }
}
