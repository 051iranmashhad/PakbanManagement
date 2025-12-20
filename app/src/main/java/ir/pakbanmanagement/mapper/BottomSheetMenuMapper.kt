package ir.pakbanmanagement.mapper

data class BottomSheetMenuMapper(
    val title: String,
    val id: String = "",
    var checked: Boolean = false,
    val key: String = "",
)
