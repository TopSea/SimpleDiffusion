package top.topsea.simplediffusion.util

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import top.topsea.simplediffusion.BuildConfig
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.ui.scripts.Script
import top.topsea.simplediffusion.ui.scripts.XYZ
import java.lang.reflect.Type

object TextUtil {
    fun toPrintJsonView(txt: String): String {
        val json = JsonParser().parse(txt).asJsonObject
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }

    fun as2String(aString: Array<String>): String {
        if (aString.isEmpty()) {
            return ""
        }

        val sb = StringBuffer("[\"")

        aString.forEachIndexed { index, str ->
            sb.append(str)
            if (index != aString.lastIndex) {
                sb.append("\", ")
            } else {
                sb.append("\"]")
            }
        }

        return sb.toString()
    }

    fun ControlNets2Request(cnModels: List<CNParam>): String {
        val sb = StringBuffer("")
        cnModels.forEach {
            sb.append(it.toRequest())
            if (it != cnModels.last()) {
                sb.append(",")
            }
        }
        return sb.toString()
    }

//    inline fun <reified T> ArrayList?.toObject(type: Type? = null): T? {
//        val gs = GsonBuilder().disableHtmlEscaping().create()
//        return if (type != null) {
//            gs.fromJson(this, type)
//        } else {
//            gs.fromJson(this, T::class.java)
//        }
//    }


    fun topsea(str: String, level: Int = Log.INFO) {
        val TAG = "TopSea:::"
        if (BuildConfig.debug) {
            when (level) {
                Log.VERBOSE -> Log.v(TAG, str)
                Log.DEBUG -> Log.d(TAG, str)
                Log.INFO -> Log.i(TAG, str)
                Log.WARN -> Log.w(TAG, str)
                Log.ERROR -> Log.e(TAG, str)
            }
        }
    }

    fun script2String(ans: Script?): String {
        return when (ans) {
            is XYZ -> "[${ans.xType}, \"${ans.xValue}\", \"\", ${ans.yType}, \"${ans.yValue}\", \"\", ${ans.zType}, \"${ans.zValue}\", \"\", ${ans.value1}, ${ans.value2}, ${ans.value3}, ${ans.value4}, ${ans.margin}]"
            else -> ""
        }
    }
}