package ir.pakbanmanagement.mapper


import com.google.gson.annotations.SerializedName

data class HistoryFineListMapper(
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("error")
    val error: String? = null, // null
    @SerializedName("message")
    val message: String? = null, // عملیات با موفقیت انجام گردید.
    @SerializedName("success")
    val success: Boolean? = null, // true
    @SerializedName("token")
    val token: String? = null
) {
    data class Data(
        @SerializedName("actionID")
        val actionID: Int? = null, // 0
        @SerializedName("contractCode")
        val contractCode: String? = null, // 465698778
        @SerializedName("contractCodeCs")
        val contractCodeCs: Any? = null, // null
        @SerializedName("contractDate")
        val contractDate: Any? = null, // null
        @SerializedName("contractGuid")
        val contractGuid: Any? = null, // null
        @SerializedName("contractId")
        val contractId: Double? = null, // 1.0
        @SerializedName("contractName")
        val contractName: String? = null, // تست پیمان
        @SerializedName("contractNo")
        val contractNo: String? = null, // 465698778
        @SerializedName("contractorCode")
        val contractorCode: String? = null, // 465698778
        @SerializedName("contractorId")
        val contractorId: Double? = null, // 1.0
        @SerializedName("contractorName")
        val contractorName: String? = null, // سامانه گستر رشد
        @SerializedName("createByUserRoleId")
        val createByUserRoleId: Double? = null, // 19.0
        @SerializedName("createdBy")
        val createdBy: Double? = null, // 12.0
        @SerializedName("createdOn")
        val createdOn: String? = null, // 2026-01-30T12:29:11.625437
        @SerializedName("encryptedData")
        val encryptedData: Any? = null, // null
        @SerializedName("entityId")
        val entityId: Double? = null, // 539.0
        @SerializedName("entityStatus")
        val entityStatus: Double? = null, // 0.0
        @SerializedName("estimateId")
        val estimateId: Double? = null, // 150.0
        @SerializedName("id")
        val id: Int? = null, // 24
        @SerializedName("isApproved")
        val isApproved: Boolean? = null, // false
        @SerializedName("isCanceled")
        val isCanceled: Boolean? = null, // false
        @SerializedName("modifiedBy")
        val modifiedBy: Any? = null, // null
        @SerializedName("modifiedOn")
        val modifiedOn: Any? = null, // null
        @SerializedName("ownerName")
        val ownerName: Any? = null, // null
        @SerializedName("receivers")
        val receivers: Any? = null, // null
        @SerializedName("referMessage")
        val referMessage: Any? = null, // null
        @SerializedName("referralStatus")
        val referralStatus: Any? = null, // null
        @SerializedName("regionName")
        val regionName: Any? = null, // null
        @SerializedName("requestDate")
        val requestDate: String? = null, // 2026-01-30T12:29:11.613437
        @SerializedName("requestDatePersian")
        val requestDatePersian: String? = null, // 1404/11/10 12:29
        @SerializedName("requestNo")
        val requestNo: String? = null, // 49041110001
        @SerializedName("rowGuid")
        val rowGuid: String? = null, // 600cc1d8-75c2-4f38-bbfe-884d8ac1479a
        @SerializedName("supSepcialFineDetails")
        val supSepcialFineDetails: List<SupSepcialFineDetail>? = null,
        @SerializedName("token")
        val token: Any? = null, // null
        @SerializedName("wflEntityAttachments")
        val wflEntityAttachments: Any? = null, // null
        @SerializedName("workflowGuid")
        val workflowGuid: Any? = null, // null
        @SerializedName("workflowInstanceGuid")
        val workflowInstanceGuid: Any? = null, // null
        @SerializedName("workflowStep")
        val workflowStep: Any? = null, // null
        @SerializedName("zoneCode")
        val zoneCode: Any? = null, // null
        @SerializedName("zoneId")
        val zoneId: Double? = null, // 97.0
        @SerializedName("zoneName")
        val zoneName: String? = null // منطقه 12
    ) {
        data class SupSepcialFineDetail(
            @SerializedName("amount")
            val amount: Double? = null, // 0.0
            @SerializedName("amountCapacity")
            val amountCapacity: Double? = null, // 360000.0
            @SerializedName("amountSelected")
            val amountSelected: Double? = null, // 0.0
            @SerializedName("approvedCapacity")
            val approvedCapacity: Double? = null, // 1000.0
            @SerializedName("capacity")
            val capacity: Double? = null, // 1000.0
            @SerializedName("completePath")
            val completePath: String? = null, // فهرست بهای 1403/رسته مدیریت شهری و روستایی/پسماند/فصل اول‌:‌نظافت عمومی دستی
            @SerializedName("createdBy")
            val createdBy: Double? = null, // 12.0
            @SerializedName("createdOn")
            val createdOn: String? = null, // 2026-01-30T12:29:11.625437
            @SerializedName("description")
            val description: Any? = null, // null
            @SerializedName("estimateDetailesId")
            val estimateDetailesId: Any? = null, // null
            @SerializedName("estimatePrice")
            val estimatePrice: Double? = null, // 360.0
            @SerializedName("features")
            val features: Any? = null, // null
            @SerializedName("financialEndPenaltyId")
            val financialEndPenaltyId: Any? = null, // null
            @SerializedName("fineViewDate")
            val fineViewDate: String? = null, // 2026-01-30T00:00:00
            @SerializedName("fineViewDatePersian")
            val fineViewDatePersian: String? = null, // 1404/11/10
            @SerializedName("hasProtest")
            val hasProtest: Boolean? = null, // false
            @SerializedName("id")
            val id: Int? = null, // 23
            @SerializedName("modifiedBy")
            val modifiedBy: Any? = null, // null
            @SerializedName("modifiedOn")
            val modifiedOn: Any? = null, // null
            @SerializedName("plCountTypeId")
            val plCountTypeId: Double? = null, // 105.0
            @SerializedName("plCountTypeTitle")
            val plCountTypeTitle: String? = null, // مترمربع
            @SerializedName("protestDescription")
            val protestDescription: Any? = null, // null
            @SerializedName("rowCode")
            val rowCode: String? = null, // 0301102010101
            @SerializedName("rowGuid")
            val rowGuid: String? = null, // 3f3a1d26-7725-4a1d-a19e-e78cea574a95
            @SerializedName("rowTitle")
            val rowTitle: String? = null, // نظافت دستي‌‌ معابر با عرض تا 15 متر
            @SerializedName("segmentId")
            val segmentId: Double? = null, // 40.0
            @SerializedName("segmentTitle")
            val segmentTitle: String? = null, // ناحیه 1 منطقه 12
            @SerializedName("selectedPointCoordinates")
            val selectedPointCoordinates: String? = null, // 36.33356416766867,59.50458189307316
            @SerializedName("supSepcialFineDetailsType")
            val supSepcialFineDetailsType: Int? = null, // 1
            @SerializedName("supSepcialFineId")
            val supSepcialFineId: Double? = null // 24.0
        )
    }
}