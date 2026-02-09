package ir.pakbanmanagement.other.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ticker.R
import com.example.ticker.core.adapter.TickerTimeAdapter
import com.example.ticker.core.adapter.ZoomCenterItemLayoutManager
import ir.pakbanmanagement.databinding.LayoutDatePickerBinding
import ir.pakbanmanagement.other.date.PersianDate

class DatePicker @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DAYS_IN_MONTH = 31
        private const val MONTHS_IN_YEAR = 12
        private const val MINUTES_IN_HOUR = 60
    }

    private val binding: LayoutDatePickerBinding =
        LayoutDatePickerBinding.inflate(LayoutInflater.from(context), this, true)

    private val dayAdapter: TickerTimeAdapter by lazy {
        TickerTimeAdapter()
    }

    private val monthAdapter: TickerTimeAdapter by lazy {
        TickerTimeAdapter()
    }

    private val yearAdapter: TickerTimeAdapter by lazy {
        TickerTimeAdapter()
    }

    private var currentlySelectedDay: String = ""
    private var currentlySelectedMonth: String = ""
    private var currentlySelectedYear: String = ""

    init {
        initConfigurations()
        initViews()
        initDayMonthYearList()
    }

    private fun initConfigurations() {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Ticker,
            defStyleAttr,
            0
        ).apply {
            try {
                // Initialize additional configurations if needed
            } finally {
                recycle()
            }
        }
    }

    private fun initViews() {
//        binding.tvDay.setOnClickListener {
//            binding.tvDay.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
//            binding.tvMonth.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//            binding.tvYear.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//        }
//
//        binding.tvMonth.setOnClickListener {
//            binding.tvMonth.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
//            binding.tvDay.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//            binding.tvYear.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//        }
//
//        binding.tvYear.setOnClickListener {
//            binding.tvYear.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
//            binding.tvDay.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//            binding.tvMonth.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
//        }
    }

    private fun initDayMonthYearList() {
        val dayList = (1..DAYS_IN_MONTH).toList()
        val monthList = (1..MONTHS_IN_YEAR).toList()
//        val yearList = (1403..1420).toList()  // Example range for years

        val persianCalendar = PersianDate()
        val currentYear = persianCalendar.shYear

        val startYear = currentYear - 10
        val endYear = currentYear + 10
        val yearList = (startYear..endYear).toList()

        initTickerRecyclerViews(
            binding.rvDay,
            dayAdapter,
            dayList,
            true,
            "day"
        )
        initTickerRecyclerViews(
            binding.rvMonth,
            monthAdapter,
            monthList,
            false,
            "month"
        )
        initTickerRecyclerViews(
            binding.rvYear,
            yearAdapter,
            yearList,
            false,
            "year"
        )
        setupTopBottomPadding()
    }

    private fun initTickerRecyclerViews(
        rv: RecyclerView,
        adapter: TickerTimeAdapter,
        unitsList: List<Int>,
        isDay: Boolean,
        type: String
    ) {
        rv.apply {
            layoutManager = ZoomCenterItemLayoutManager(context)
            this.adapter = adapter
        }
        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(rv)

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateTextColorForSelectedTimeText(
                        recyclerView,
                        ir.pakbanmanagement.R.color.color_black,
                        type
                    )
                } else {
                    updateTextColorForSelectedTimeText(
                        recyclerView,
                        ir.pakbanmanagement.R.color.color_gray,
                        type
                    )
                }
            }

            private fun updateTextColorForSelectedTimeText(
                recyclerView: RecyclerView,
                @ColorRes textColorRes: Int,
                type: String
            ) {
                val snappedChildView =
                    linearSnapHelper.findSnapView(recyclerView.layoutManager) ?: return
                val snappedViewHolder = rv.getChildViewHolder(snappedChildView)
                if (snappedViewHolder is TickerTimeAdapter.DefaultTickerViewHolder) {
                    snappedViewHolder.binding.root.setTextColor(
                        ResourcesCompat.getColor(resources, textColorRes, null)
                    )
                    val timeString = snappedViewHolder.binding.root.text.toString()
                    updateCurrentlySelectedValues(type, timeString, textColorRes)
                }
            }
        })


        adapter.submitList(unitsList)
        scrollToCurrentTime(rv, isDay)
    }

    private fun updateCurrentlySelectedValues(
        type: String,
        timeString: String,
        @ColorRes appliedColorRes: Int
    ) {
        if (appliedColorRes == ir.pakbanmanagement.R.color.color_black) {
            when (type) {
                "day" -> {
                    currentlySelectedDay = timeString
                }

                "month" -> {
                    currentlySelectedMonth = timeString
                }

                "year" -> {
                    currentlySelectedYear = timeString
                }
            }
        }
    }

    private fun setupTopBottomPadding() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.root.height
                val padding = height / 2 - 20
                binding.rvDay.setPadding(0, padding, 0, padding)
                binding.rvMonth.setPadding(0, padding, 0, padding)
                binding.rvYear.setPadding(0, padding, 0, padding)

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun scrollToCurrentTime(rv: RecyclerView, isDay: Boolean) {
        val scrollRunnable = Runnable {
            val position = when {
                isDay -> currentlySelectedDay.toIntOrNull() ?: 1

                currentlySelectedMonth.isNotEmpty() -> currentlySelectedMonth.toIntOrNull() ?: 1

                else -> currentlySelectedYear.toIntOrNull() ?: 1403
            }
            rv.smoothScrollToPosition(position)
        }
        rv.postDelayed(scrollRunnable, 100)
    }

    /**
     * Returns the currently selected date
     * @param format: Return format for date, default DD/MM/YYYY
     */
    fun getCurrentlySelectedDate(format: String = "DD/MM/YYYY"): String {
        return format
            .replace("DD", currentlySelectedDay)
            .replace("MM", currentlySelectedMonth)
            .replace("YYYY", currentlySelectedYear)
    }

    fun getCurrentlySelectedDate2(block: (String, String, String) -> Unit) {
        block.invoke(currentlySelectedDay, currentlySelectedMonth, currentlySelectedYear)
    }

    fun getCurrentlySelectedDate3(): String {
        return "$currentlySelectedYear/$currentlySelectedMonth/$currentlySelectedDay"
    }

    /**
     * Sets the initially selected date for the picker
     * Format DD/MM/YYYY
     */
    fun setInitialSelectedDate(initialDate: String) {
        // تجزیه تاریخ شمسی
        val dateParts = initialDate.split("/")
        if (dateParts.size < 3) return

        val year = dateParts[0].toIntOrNull() ?: return
        val month = dateParts[1].toIntOrNull() ?: return
        val day = dateParts[2].toIntOrNull() ?: return

        val persianCalendar = PersianDate()
        val currentYear = persianCalendar.shYear

        val startYear = currentYear - 10
        val endYear = currentYear + 10
        mYearList = (startYear..endYear).toList()

        // اسکرول به روز، ماه، سال شمسی
        scrollToSelectedDay(day)
        scrollToSelectedMonth(month)
        scrollToSelectedYear(year)
    }

    private val mDayList = (1..DAYS_IN_MONTH).toList()
    private val mMonthList = (1..MONTHS_IN_YEAR).toList()
    private var mYearList = (1403..1420).toList()  // Example range for years

    private fun scrollToSelectedDay(day: Int) {
//        val position = day - 1 // از صفر شروع بشه

        val position = mDayList.indexOf(day)

        binding.rvDay.postDelayed({
            binding.rvDay.smoothScrollToPosition(position)
        }, 100)
    }

    private fun scrollToSelectedMonth(month: Int) {
//        val position = month - 1 // از صفر شروع بشه

        val position = mMonthList.indexOf(month)

        binding.rvMonth.postDelayed({
            binding.rvMonth.smoothScrollToPosition(position)
        }, 100)

    }

    private fun scrollToSelectedYear(year: Int) {
//        val position = year - 1403 // فرض کنیم سال‌ها از 1300 شروع می‌شوند

        val position = mYearList.indexOf(year)

        binding.rvYear.postDelayed({
            binding.rvYear.smoothScrollToPosition(position)
        }, 100)
    }
}
