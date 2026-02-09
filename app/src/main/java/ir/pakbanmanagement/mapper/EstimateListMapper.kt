package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class EstimateListMapper(
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
        @SerializedName("class")
        val `class`: Any? = null, // null
        @SerializedName("description")
        val description: String? = null, // 0301102010101
        @SerializedName("guid")
        val guid: Any? = null, // null
        @SerializedName("reserve")
        val reserve: Double? = null, // 1484958.0
        @SerializedName("title")
        val title: String? = null, // 0301102010101 - نظافت دستي‌‌ معابر با عرض تا 15 متر
        @SerializedName("value")
        val value: Int? = null // 1236
    )
}
