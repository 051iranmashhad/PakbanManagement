package ir.pakbanmanagement.mapper

import com.google.gson.annotations.SerializedName
import ir.pakbanmanagement.other.getLocalDate
import ir.pakbanmanagement.other.getLocalTime

data class ImageListMapper(
    @SerializedName("Base64Data")
    val base64Data: String,
    @SerializedName("ContentType")
    val contentType: String = "image/jpeg", // image/jpeg
    @SerializedName("Description")
    val description: String = "${getLocalDate()} - ${getLocalTime()}", // تست
    @SerializedName("FileName")
    val fileName: String = "${System.currentTimeMillis()}.jpg"
)
