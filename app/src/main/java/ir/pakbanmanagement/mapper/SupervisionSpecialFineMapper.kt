package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class SupervisionSpecialFineMapper(
    @SerializedName("success")
    val success: Boolean? = null, // false
    @SerializedName("message")
    val message: String? = null
)
