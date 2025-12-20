package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class TokenMapper(
    @SerializedName("Token")
    val token: String? = null,
)
