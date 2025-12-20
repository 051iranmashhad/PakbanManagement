package ir.pakbanmanagement.other

import android.annotation.SuppressLint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Locale
import java.util.UUID
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkManager {

    private const val DEFAULT_URL = "https://google.com"
    private const val DEFAULT_CONNECT_TIME_OUT = 60_000
    private const val DEFAULT_READ_TIME_OUT = 60_000
    private const val DEFAULT_HAS_BASE_64 = false
    private val RANGE_STATUS_CODE = 200..299
    private lateinit var headerFields: Map<String?, List<String>>

    internal suspend fun request(
        url: String? = null,
        method: String? = null,
        timeOut: Int? = null,
        hashHeaders: HashMap<String, String>? = null,
        body: String? = null,
        hasByteArray: Boolean? = null,
        hasBase64: Boolean? = null,
    ): JSONObject {
        return withContext(Dispatchers.IO) {
//                delay(TIME_DELAY_REQUEST)
            val jobj = JSONObject()
            val urlParser = url?.toEnglishNumber()
            val urlAddress = URL(urlParser ?: DEFAULT_URL)
            var httpURLConnection: HttpURLConnection? = null
            var inputStreamReader: InputStreamReader? = null
            var bufferedReader: BufferedReader? = null
            val stringBuffer = StringBuffer()
            val inputStream: InputStream?

            try {
                // handle ssl handshake
                handleSSLHandshake()

                httpURLConnection = run {
                    if (urlAddress.protocol?.lowercase(Locale.getDefault()) == Network.HTTPS) urlAddress.openConnection() as HttpsURLConnection else urlAddress.openConnection() as HttpURLConnection
                }

                // time and method
                httpURLConnection.requestMethod = method ?: Method.GET
                httpURLConnection.connectTimeout = timeOut ?: DEFAULT_CONNECT_TIME_OUT
                httpURLConnection.readTimeout = timeOut ?: DEFAULT_READ_TIME_OUT
                httpURLConnection.doOutput = hasByteArray == true

                // set header
                hashHeaders?.forEach {
                    val key = it.key.toEnglishNumber()
                    val value = it.value.toEnglishNumber()
                    httpURLConnection.setRequestProperty(key, value)
                }

                // set body
                if (method == Method.POST || method == Method.PUT || method == Method.DELETE || method == Method.PATCH) {
                    body?.let {
                        if (hasByteArray == true) {
                            httpURLConnection.outputStream?.write(body.toDecodeByteArray())
                        } else {
                            val os: OutputStream? = httpURLConnection.outputStream
                            val osw = OutputStreamWriter(os, Network.UTF_8)
                            val bodyParser = body.toEnglishNumber()
                            osw.write(bodyParser)
                            osw.flush()
                            osw.close()
                            os?.close()
                        }
                    }
                }

                // get response
                if (httpURLConnection.responseCode in RANGE_STATUS_CODE) {
                    inputStream = run {
                        if (httpURLConnection.contentEncoding?.lowercase(Locale.getDefault()) == Network.GZIP) {
                            GZIPInputStream(httpURLConnection.inputStream)
                        } else {
                            httpURLConnection.inputStream
                        }
                    }

                    if (!(hasBase64 ?: DEFAULT_HAS_BASE_64)) {
                        inputStreamReader = InputStreamReader(inputStream, Network.UTF_8)
                        bufferedReader = BufferedReader(inputStreamReader)

                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            stringBuffer.append(line)
                        }
                    }

                    jobj.put(Network.RESPONSE_STATUS, "${httpURLConnection.responseCode}")
                    jobj.put(Network.RESPONSE_BODY, run {
                        if (inputStream != null) {
                            if (hasBase64 == true) inputStream.encodeInputStreamToBase64()
                            else stringBuffer.toString()
                        } else "no body"
                    })
                    jobj.put(Network.RESPONSE_HEADER, run {
                        if (!httpURLConnection.headerFields.isNullOrEmpty()) {
                            headerFields = httpURLConnection.headerFields
                            val builder = kotlin.text.StringBuilder()
                            builder.append(httpURLConnection.responseCode).append(" ")
                                .append(httpURLConnection.responseMessage).append("\n")
                            val map: Map<String?, List<String>> = httpURLConnection.headerFields!!
                            for ((key, headerValues) in map) {
                                if (key == null) continue
                                builder.append(key).append(": ")
                                val it = headerValues.iterator()
                                if (it.hasNext()) {
                                    builder.append(it.next())
                                    while (it.hasNext()) {
                                        builder.append(", ").append(it.next())
                                    }
                                }
                                builder.append("\n")
                            }
                            builder
                        } else "no header"

                    })
                    return@withContext jobj
                } else {
                    inputStream = run {
                        if (httpURLConnection.contentEncoding?.lowercase(Locale.getDefault()) == Network.GZIP) {
                            GZIPInputStream(httpURLConnection.errorStream)
                        } else {
                            httpURLConnection.errorStream
                        }
                    }

                    inputStreamReader = InputStreamReader(inputStream, Network.UTF_8)
                    bufferedReader = BufferedReader(inputStreamReader)

                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                    }

                    jobj.put(Network.RESPONSE_STATUS, "${httpURLConnection.responseCode}")
                    jobj.put(Network.RESPONSE_BODY, run {
                        if (inputStream != null) {
                            stringBuffer.toString()
                        } else "no body"

                    })
                    jobj.put(Network.RESPONSE_HEADER, run {
                        if (!httpURLConnection.headerFields.isNullOrEmpty()) {
                            val builder = kotlin.text.StringBuilder()
                            builder.append(httpURLConnection.responseCode).append(" ")
                                .append(httpURLConnection.responseMessage).append("\n")
                            val map: Map<String?, List<String>> = httpURLConnection.headerFields!!
                            for ((key, headerValues) in map) {
                                if (key == null) continue
                                builder.append(key).append(": ")
                                val it = headerValues.iterator()
                                if (it.hasNext()) {
                                    builder.append(it.next())
                                    while (it.hasNext()) {
                                        builder.append(", ").append(it.next())
                                    }
                                }
                                builder.append("\n")
                            }
                            builder
                        } else "no header"

                    })
                    return@withContext jobj
                }

            } catch (e: Exception) {
                jobj.put(Network.RESPONSE_STATUS, "0")
                jobj.put(Network.RESPONSE_BODY, "error body $e")
                jobj.put(Network.RESPONSE_HEADER, "error header")
            } finally {
                bufferedReader?.close()
                inputStreamReader?.close()
                httpURLConnection?.disconnect()

                "${Network.RESPONSE_STATUS} => ${jobj.getString(Network.RESPONSE_STATUS)}".logV()
                "${Network.RESPONSE_BODY} => ${jobj.getString(Network.RESPONSE_BODY)}".logV()
                "${Network.RESPONSE_HEADER} => ${jobj.getString(Network.RESPONSE_HEADER)}".logV()
            }
        }
    }

    @SuppressLint("TrustAllX509TrustManager")
    private fun handleSSLHandshake() {
        val trustAllCertificates: Array<TrustManager> = arrayOf(
            @SuppressLint("CustomX509TrustManager") object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?, authType: String?,
                ) {
                }
            })

        val sslContext: SSLContext = SSLContext.getInstance(Network.TLS)
        sslContext.init(null, trustAllCertificates, SecureRandom())

        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    internal suspend fun httpParser(
        block: (Constant.ResultWrapper) -> Unit,
        data: suspend () -> JSONObject,
    ) {
        block(Constant.ResultWrapper.Loading)
        val obj = data.invoke()
        when {
            obj.getString(Network.RESPONSE_STATUS)
                .toInt() in RANGE_STATUS_CODE -> {
                block(
                    Constant.ResultWrapper.Success(
                        body = obj.getString(Network.RESPONSE_BODY),
                        code = obj.getString(Network.RESPONSE_STATUS)
                            .toInt(),
                        header = obj.getString(Network.RESPONSE_HEADER)
                    )
                )
            }

            else -> {
                block(
                    Constant.ResultWrapper.Error(
                        body = obj.getString(Network.RESPONSE_BODY),
                        code = obj.getString(Network.RESPONSE_STATUS).toInt(),
                        header = obj.getString(Network.RESPONSE_HEADER)
                    )
                )
            }
        }
    }

    internal suspend fun uploadFile(
        url: String? = null,
        method: String? = null,
        hashHeaders: HashMap<String, String?>? = null,
        hashParams: HashMap<String, String?>? = null,
        fileField: String? = null,
        file: File? = null,
        progress: (Long) -> Unit,
    ): JSONObject {
        return withContext(Dispatchers.IO) {
//            delay(TIME_DELAY_REQUEST)
            val jobj = JSONObject()
            val urlAddress = URL(url ?: DEFAULT_URL)
            var httpURLConnection: HttpURLConnection? = null
            val inputStreamReader: InputStreamReader?
            val bufferedReader: BufferedReader?
            val stringBuffer = StringBuffer()
            val inputStream: InputStream?

            try {
                // handle ssl handshake
                handleSSLHandshake()

                httpURLConnection = run {
                    if (urlAddress.protocol?.lowercase(Locale.getDefault()) == Network.HTTPS) urlAddress.openConnection() as HttpsURLConnection else urlAddress.openConnection() as HttpURLConnection
                }
                val boundary = UUID.randomUUID().toString()

                httpURLConnection.doOutput = true
                httpURLConnection.requestMethod = method
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive")
                httpURLConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=$boundary"
                )

                // Add custom headers
                hashHeaders?.forEach { (key, value) ->
                    httpURLConnection.setRequestProperty(key, value)
                }

                val outputStream = DataOutputStream(httpURLConnection.outputStream)

                // Set additional parameters if needed
                hashParams?.forEach { (key, value) ->
                    outputStream.writeBytes("--$boundary\r\n")
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
                    outputStream.write(value?.toByteArray(Charsets.UTF_8))
                    outputStream.writeBytes("\r\n")
                }

                // Set file part
                outputStream.writeBytes("--$boundary\r\n")
                outputStream.writeBytes("Content-Disposition: form-data; name=\"${fileField}\"; filename=\"${(file?.name.ifNullOrEmpty()).toEnglishNumber()}\"\r\n")
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n")

                val fileInputStream = FileInputStream(file)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                var totalBytesRead = 0L
                val fileSize = file?.length() ?: 0

                while (fileInputStream.read(buffer).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    withContext(Dispatchers.Main) {
                        // Emit progress
                        progress.invoke((totalBytesRead * 100) / fileSize)
                    }
                }

                fileInputStream.close()

                // Finish the request
                outputStream.writeBytes("\r\n--$boundary--\r\n")
                outputStream.flush()
                outputStream.close()

                // Check the response code if needed
                if (httpURLConnection.responseCode in RANGE_STATUS_CODE) {
                    inputStream = run {
                        if (httpURLConnection.contentEncoding?.lowercase(Locale.getDefault()) == Network.GZIP) {
                            GZIPInputStream(httpURLConnection.inputStream)
                        } else {
                            httpURLConnection.inputStream
                        }
                    }

                    inputStreamReader = InputStreamReader(inputStream, Network.UTF_8)
                    bufferedReader = BufferedReader(inputStreamReader)

                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                    }

                    jobj.put(Network.RESPONSE_STATUS, "${httpURLConnection.responseCode}")
                    jobj.put(Network.RESPONSE_BODY, run {
                        if (inputStream != null) stringBuffer.toString() else "no body"
                    })
                    jobj.put(Network.RESPONSE_HEADER, run {
                        if (!httpURLConnection.headerFields.isNullOrEmpty()) {
                            Constant.headerFields = httpURLConnection.headerFields
                            val builder = kotlin.text.StringBuilder()
                            builder.append(httpURLConnection.responseCode).append(" ")
                                .append(httpURLConnection.responseMessage).append("\n")
                            val map: Map<String?, List<String>> = httpURLConnection.headerFields!!
                            for ((key, headerValues) in map) {
                                if (key == null) continue
                                builder.append(key).append(": ")
                                val it = headerValues.iterator()
                                if (it.hasNext()) {
                                    builder.append(it.next())
                                    while (it.hasNext()) {
                                        builder.append(", ").append(it.next())
                                    }
                                }
                                builder.append("\n")
                            }
                            builder
                        } else "no header"

                    })
                    return@withContext jobj
                } else {
                    inputStream = run {
                        if (httpURLConnection.contentEncoding?.lowercase(Locale.getDefault()) == Network.GZIP) {
                            GZIPInputStream(httpURLConnection.errorStream)
                        } else {
                            httpURLConnection.errorStream
                        }
                    }

                    inputStreamReader = InputStreamReader(inputStream, Network.UTF_8)
                    bufferedReader = BufferedReader(inputStreamReader)

                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                    }

                    jobj.put(Network.RESPONSE_STATUS, "${httpURLConnection.responseCode}")
                    jobj.put(Network.RESPONSE_BODY, run {
                        if (inputStream != null) {
                            stringBuffer.toString()
                        } else "no body"

                    })
                    jobj.put(Network.RESPONSE_HEADER, run {
                        if (!httpURLConnection.headerFields.isNullOrEmpty()) {
                            val builder = kotlin.text.StringBuilder()
                            builder.append(httpURLConnection.responseCode).append(" ")
                                .append(httpURLConnection.responseMessage).append("\n")
                            val map: Map<String?, List<String>> = httpURLConnection.headerFields!!
                            for ((key, headerValues) in map) {
                                if (key == null) continue
                                builder.append(key).append(": ")
                                val it = headerValues.iterator()
                                if (it.hasNext()) {
                                    builder.append(it.next())
                                    while (it.hasNext()) {
                                        builder.append(", ").append(it.next())
                                    }
                                }
                                builder.append("\n")
                            }
                            builder
                        } else "no header"

                    })
                    return@withContext jobj
                }
            } catch (e: Exception) {
                jobj.put(Network.RESPONSE_STATUS, "0")
                jobj.put(Network.RESPONSE_BODY, "error body $e")
                jobj.put(Network.RESPONSE_HEADER, "error header")
            } finally {
                httpURLConnection?.disconnect()

                "${Network.RESPONSE_STATUS} => ${jobj.getString(Network.RESPONSE_STATUS)}".logV()
                "${Network.RESPONSE_BODY} => ${jobj.getString(Network.RESPONSE_BODY)}".logV()
                "${Network.RESPONSE_HEADER} => ${jobj.getString(Network.RESPONSE_HEADER)}".logV()
            }
        }
    }

    sealed class ResultWrapper {
        data object Loading : ResultWrapper()
        data class Success(val body: String = "", val code: Int = 0, val header: String = "") :
            ResultWrapper()

        data class Error(val body: String = "", val code: Int = 0, val header: String = "") :
            ResultWrapper()
    }

    object Network {
        internal const val HTTPS = "https"
        internal const val RESPONSE_STATUS = "responseStatus"
        internal const val RESPONSE_BODY = "responseBody"
        internal const val RESPONSE_HEADER = "responseHeader"
        internal const val CONTENT_LENGTH = "content_length"
        internal const val CHARSET = "Charset"
        internal const val UTF_8 = "UTF-8"
        internal const val TLS = "TLS"
        internal const val GZIP = "gzip"
        internal const val AUTHORIZATION = "Authorization"
        internal const val CONTENT_TYPE = "Content-Type"
        internal const val APPLICATION_JSON = "application/json"
        internal const val USER_AGENT = "User-Agent"
    }

    object Method {
        internal const val GET = "GET"
        internal const val POST = "POST"
        internal const val PUT = "PUT"
        internal const val DELETE = "DELETE"
        internal const val PATCH = "PATCH"
    }
}
