package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class PriceListSeasonMapper(
    @SerializedName("data")
    val `data`: List<Data>? = null,
    @SerializedName("error")
    val error: String? = null, // null
    @SerializedName("message")
    val message: String? = null, // null
    @SerializedName("success")
    val success: Boolean? = null, // true
    @SerializedName("token")
    val token: String? = null // null
) {
    data class Data(
        @SerializedName("code")
        val code: String? = null, // 01
        @SerializedName("createdBy")
        val createdBy: Double? = null, // 0.0
        @SerializedName("createdOn")
        val createdOn: String? = null, // 2024-07-02T12:09:02.048934
        @SerializedName("description")
        val description: Any? = null, // null
        @SerializedName("id")
        val id: Int? = null, // 1
        @SerializedName("isWorkShop")
        val isWorkShop: Boolean? = null, // false
        @SerializedName("modifiedBy")
        val modifiedBy: Any? = null, // null
        @SerializedName("modifiedOn")
        val modifiedOn: Any? = null, // null
        @SerializedName("priceListFieldId")
        val priceListFieldId: Int? = null, // 0
        @SerializedName("priceListFieldRowGuid")
        val priceListFieldRowGuid: Any? = null, // null
        @SerializedName("priceListRowDTOs")
        val priceListRowDTOs: List<PriceRowDTOs>? = null,
        @SerializedName("rowGuid")
        val rowGuid: String? = null, // 133883ae-4b4f-4c27-8230-76a0da3c0822
        @SerializedName("title")
        val title: String? = null, // فصل اول‌:‌نظافت عمومی دستی
        @SerializedName("titleKey")
        val titleKey: String? = null // 10101
    ) {
        data class PriceRowDTOs(
            @SerializedName("code")
            val code: String? = null, // 0101
            @SerializedName("completeCode")
            val completeCode: Any? = null, // null
            @SerializedName("createdBy")
            val createdBy: Double? = null, // 0.0
            @SerializedName("createdOn")
            val createdOn: String? = null, // 0001-01-01T00:00:00
            @SerializedName("description")
            val description: Any? = null, // null
            @SerializedName("id")
            val id: Int? = null, // 1
            @SerializedName("isSelected")
            val isSelected: Any? = null, // null
            @SerializedName("isSelectedAll")
            val isSelectedAll: Any? = null, // null
            @SerializedName("modifiedBy")
            val modifiedBy: Any? = null, // null
            @SerializedName("modifiedOn")
            val modifiedOn: Any? = null, // null
            @SerializedName("price")
            val price: Any? = null, // null
            @SerializedName("priceCountTypeId")
            val priceCountTypeId: Int? = null, // 0
            @SerializedName("priceCountTypeRowGuid")
            val priceCountTypeRowGuid: Any? = null, // null
            @SerializedName("priceListSeasonId")
            val priceListSeasonId: Int? = null, // 0
            @SerializedName("priceListSeasonRowGuid")
            val priceListSeasonRowGuid: Any? = null, // null
            @SerializedName("rowGuid")
            val rowGuid: Any? = null, // null
            @SerializedName("stateId")
            val stateId: Any? = null, // null
            @SerializedName("title")
            val title: String? = null // نظافت دستي‌‌ معابر با عرض تا 15 متر
        )
    }
}
