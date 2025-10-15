package ir.pakbanmanagement.mapper

import ir.pakbanmanagement.other.Constant

data class MenuMapper(
    val id: String = Constant.Key.EMPTY,
    val title: String = Constant.Key.EMPTY,
    val icon: Int = 0,
    var hasChecked: Boolean = false,
    var hasVisibility: Boolean = false,
)
