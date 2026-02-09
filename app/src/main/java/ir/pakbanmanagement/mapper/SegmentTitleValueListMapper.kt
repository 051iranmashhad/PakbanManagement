package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class SegmentTitleValueListMapper(
    @SerializedName("data")
    val `data`: List<Data>? = null,
    @SerializedName("error")
    val error: Any? = null, // null
    @SerializedName("message")
    val message: String? = null, // عملیات با موفقیت انجام گردید.
    @SerializedName("success")
    val success: Boolean? = null, // true
    @SerializedName("token")
    val token: String? = null
) {
    data class Data(
        @SerializedName("class")
        val `class`: Any? = null, // null
        @SerializedName("description")
        val description: Any? = null, // null
        @SerializedName("guid")
        val guid: Any? = null, // null
        @SerializedName("reserve")
        val reserve: Any? = null, // null
        @SerializedName("title")
        val title: String? = null, // ناحیه 1 منطقه 12 (منطقه 12)
        @SerializedName("value")
        val value: Double? = null // 40.0
    )
}
