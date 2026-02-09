package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class FinancialEndPenaltyListMapper(
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
        val description: String? = null, // 0.25 * میانگین سرانه نظافت دستی (26247 مترمربع) * میانگین بهای واحد ردیف‌های نظافت دستی معابر در فهرست منضم به پیمان
        @SerializedName("guid")
        val guid: Any? = null, // null
        @SerializedName("reserve")
        val reserve: Double? = null, // 2116164.4
        @SerializedName("title")
        val title: String? = null, // عدم تحویل فیش حقوقی به کارگران
        @SerializedName("value")
        val value: Int? = null // 1
    )
}
