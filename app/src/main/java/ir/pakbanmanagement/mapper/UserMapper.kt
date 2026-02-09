package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class UserMapper(
    var contractTitle: String? = null,
    var contractId: Int? = null,

    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("success")
    val success: Boolean? = null, // true
    @SerializedName("token")
    val token: String? = null,
) {
    data class Data(
        @SerializedName("address")
        val address: Any? = null, // null
        @SerializedName("firstName")
        val firstName: String? = null, // علی
        @SerializedName("hasTwoFactorAuthentcator")
        val hasTwoFactorAuthentcator: Any? = null, // null
        @SerializedName("id")
        val id: Int? = null, // 12
        @SerializedName("isAlive")
        val isAlive: Any? = null, // null
        @SerializedName("isValid")
        val isValid: Any? = null, // null
        @SerializedName("isValidChangeDate")
        val isValidChangeDate: Any? = null, // null
        @SerializedName("lastName")
        val lastName: String? = null, // محمدی
        @SerializedName("mobileNum")
        val mobileNum: String? = null, // 068546548
        @SerializedName("newLogin")
        val newLogin: Any? = null, // null
        @SerializedName("roles")
        val roles: Any? = null, // null
        @SerializedName("signaturePath")
        val signaturePath: Any? = null, // null
        @SerializedName("userRoles")
        val userRoles: Any? = null, // null
        @SerializedName("username")
        val username: String? = null, // 5456454@manmail.ir
    )
}
