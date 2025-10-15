package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class UserMapper(
    @SerializedName("token")
    var token: String? = null,
)
