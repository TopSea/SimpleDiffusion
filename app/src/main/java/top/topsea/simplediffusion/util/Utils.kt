package top.topsea.simplediffusion.util

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import java.util.Calendar
import java.util.Date


const val today_format = "HH:mm"
const val before_format = "yyyy-MM-dd"

const val me = "me"
const val droid = "ChatGLM-6B"

@SuppressLint("SimpleDateFormat")
fun getCurrTime(): String {
    val time = System.currentTimeMillis()
    val format = SimpleDateFormat(Constant.date_format)
    val d1 = Date(time)
    return format.format(d1)
}

@SuppressLint("SimpleDateFormat")
fun getFormatByDate(date: java.sql.Date): String {
    val todayFormat = SimpleDateFormat(today_format)
    val beforeFormat = SimpleDateFormat(before_format)

    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.get(Calendar.YEAR)


    val curr = System.currentTimeMillis()
    val dNow = Date(curr)
    val calendarNow = Calendar.getInstance()
    calendarNow.time = dNow

    return if (calendarNow.get(Calendar.YEAR) < calendar.get(Calendar.YEAR)) {              // 间隔大于一年直接用日期
        beforeFormat.format(date)
    } else {              // 间隔小于一年大于一天直接用日期
        if (calendarNow.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) {
            beforeFormat.format(date)
        } else {
            // 还要把下午的小时数减掉 12，嫌麻烦先将就着吧
//            if (calendarNow.get(Calendar.AM_PM) == Calendar.AM) {             // 间隔小于一天的情况
//                todayFormat.format(date) + " AM"
//            } else {
//                todayFormat.format(date) + " PM"
//            }
            todayFormat.format(date)
        }
    }

}

@Composable
fun getWidthDp(): Dp {
    val context = LocalContext.current
    val wp = context.resources.displayMetrics.widthPixels
    return with(LocalDensity.current) { wp.toDp() }
}

@Composable
fun getHeightDp(): Dp {
    val context = LocalContext.current
    val hp = context.resources.displayMetrics.heightPixels
    return with(LocalDensity.current) { hp.toDp() }
}

@Composable
fun getDpSize(width: Int, height: Int): DpSize {
    val hp = with(LocalDensity.current) { height.toDp() }
    val wp = with(LocalDensity.current) { width.toDp() }
    return DpSize(wp, hp)
}

@Composable
fun getDpSize(size: Size): DpSize {
    val hp = with(LocalDensity.current) { size.height.toDp() }
    val wp = with(LocalDensity.current) { size.width.toDp() }
    return DpSize(wp, hp)
}