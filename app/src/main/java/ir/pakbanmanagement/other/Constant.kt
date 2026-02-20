package ir.pakbanmanagement.other

import ir.pakbanmanagement.R
import ir.pakbanmanagement.mapper.MenuMapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.jvm.java

object Constant {

    internal val COROUTINE_EXCEPTION_HANDLER by lazy {
        CoroutineExceptionHandler { _, exception ->
            "CoroutineExceptionHandler $exception".logV(this::class.java)
            throw kotlin.IllegalStateException(exception)
        }
    }

    internal const val PREF_MANAGER = "pref_manager"
    internal const val DATASTORE_TOKEN = "token"
    internal const val DATASTORE_BASE_URL = "base_url"
    internal const val DATASTORE_PROFILE = "profile"

    internal const val LIMIT_PAGE_SIZE: Int = 30
    internal const val OFFSET_PAGE_SIZE: Int = 30

    internal lateinit var headerFields: Map<String?, List<String>>

    object MenuKey {
        internal const val PROFILE = "profile"
        internal const val FINE_LIST = "fine_list"
        internal const val LOG_OUT = "log_out"
    }

    internal val menuList = mutableListOf(
        MenuMapper(
            id = MenuKey.PROFILE, title = "پروفایل", icon = R.drawable.ic_user
        ),
        MenuMapper(
            id = MenuKey.FINE_LIST, title = "تاریخچه جرایم", icon = R.drawable.ic_history
        ),
        MenuMapper(
            id = MenuKey.LOG_OUT, title = "خروج", icon = R.drawable.ic_log_out
        ),
    )

    object HomeMenuKey {
        internal const val PRODUCTS = "products"
        internal const val IMAGE_GALLERY = "image_gallery"
        internal const val USERS = "users"
        internal const val SHOP = "shop"
        internal const val SMS = "sms"
        internal const val PRINT_DESIGN = "print_design"
        internal const val SETTINGS = "settings"
        internal const val ACCOUNTING = "accounting"
    }

    internal val menuHomeList = mutableListOf(
        MenuMapper(
            id = HomeMenuKey.USERS,
            title = "کاربران",
            icon = R.drawable.ic_user,
            hasVisibility = true
        ),
    )

    object Key {
        internal const val Q = "q"
        internal const val SEARCH = "search"

        internal const val NAME = "name"

        internal const val CODE = "code"

        internal const val SUCCESS = "success"
        internal const val PENDING = "pending"
        internal const val FAILED = "failed"

        internal const val TOKEN = "token"
        internal const val DATA = "data"

        internal const val DATE = "date"

        internal const val MOBILE_NUMBER = "mobile_number"

        internal const val FILE = "file"

        internal const val ADD = "add"
        internal const val INSERT = "insert"
        internal const val UPDATE = "update"
        internal const val DELETE = "delete"
        internal const val REMOVE = "remove"
        internal const val LIST = "list"
        internal const val ITEM = "item"

        internal const val ID = "id"
        internal const val BODY = "body"
        internal const val API_KEY = "Api-Key"

        internal const val BMP = "bmp"
        internal const val PNG = "png"
        internal const val JPEG = "jpeg"
        internal const val JPG = "jpg"
        internal const val PDF = "pdf"
        internal const val HTML = "html"

        internal const val KEY = "key"
        internal const val VALUE = "value"

        internal const val LATITUDE = "latitude"
        internal const val LONGITUDE = "longitude"

        internal const val OK = "OK"
        internal const val MAPPER = "mapper"
        internal const val LIMIT = "limit"
        internal const val OFFSET = "offset"

        internal const val TRUE = "True"
        internal const val FALSE = "False"

        internal const val EMPTY = ""

        internal const val FILE_URL = "file_url"
        internal const val FILE_DIRECTORY = "file_directory"
        internal const val FILE_NAME = "file_name"
        internal const val FILE_TYPE = "file_type"
        internal const val FILE_METHOD = "file_method"
        internal const val FILE_BODY = "file_body"
    }

    object WeekDay {
        internal const val DAY = "day"
        internal const val SATURDAY = "saturday"
        internal const val SUNDAY = "sunday"
        internal const val MONDAY = "monday"
        internal const val TUESDAY = "tuesday"
        internal const val WEDNESDAY = "wednesday"
        internal const val THURSDAY = "thursday"
        internal const val FRIDAY = "friday"
    }

    sealed class ResultWrapper {
        data object Loading : ResultWrapper()
        data class Success(
            val body: String = Constant.Key.EMPTY,
            val code: Int = 0,
            val header: String = Constant.Key.EMPTY
        ) :
            ResultWrapper()

        data class Error(val body: String = "", val code: Int = 0, val header: String = "") :
            ResultWrapper()
    }

    sealed class ToastType {
        data object Success : ToastType()
        data object Warning : ToastType()
        data object Error : ToastType()
        data object Info : ToastType()
    }
}
