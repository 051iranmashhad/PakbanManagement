package ir.pakbanmanagement.presentation.main.repository

import ir.pakbanmanagement.BuildConfig
import ir.pakbanmanagement.other.NetworkManager
import ir.pakbanmanagement.other.base.BaseRepository
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
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToekn()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }

    internal suspend fun getPriceListSeason(map: HashMap<String, String>): JSONObject {
        return NetworkManager.request(
            url = "${BuildConfig.BASE_URL}/api/MobileApi/GetPriceListSeason".also { it.logV() },
            method = NetworkManager.Method.GET,
            hashHeaders = hashMapOf<String, String>().apply {
                put(NetworkManager.Network.AUTHORIZATION, "Bearer ${getToekn()}")
                put(NetworkManager.Network.CONTENT_TYPE, NetworkManager.Network.APPLICATION_JSON)
            }
        )
    }
}
