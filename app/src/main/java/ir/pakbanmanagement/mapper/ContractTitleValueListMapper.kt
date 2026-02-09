package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class ContractTitleValueListMapper(
    @SerializedName("data")
    val `data`: List<Data>? = null,
    @SerializedName("error")
    val error: Any? = null, // null
    @SerializedName("message")
    val message: Any? = null, // null
    @SerializedName("success")
    val success: Boolean? = null, // true
    @SerializedName("token")
    val token: Any? = null // null
) {
    data class Data(
        @SerializedName("categoryGroupId")
        val categoryGroupId: Any? = null, // null
        @SerializedName("class")
        val `class`: String? = null, // Active
        @SerializedName("description")
        val description: String? = null, // 11a48194-b16b-4fc2-8387-ffdf06add625
        @SerializedName("key")
        val key: Any? = null, // null
        @SerializedName("sortOrder")
        val sortOrder: Int? = null, // 12
        @SerializedName("title")
        val title: String? = null, // تست پیمان
        @SerializedName("value")
        val value: Int? = null // 1
    )
}
