package top.topsea.simplediffusion.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import top.topsea.simplediffusion.data.param.BasicParam
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.SavableImage
import top.topsea.simplediffusion.data.param.SavableImgParam
import top.topsea.simplediffusion.data.param.SavableTxtParam
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.ui.scripts.Script
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.sql.Date


class SerialConverter {

    @TypeConverter
    fun toByteArray(serializable: Script?): ByteArray? {
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        var objectOutputStream: ObjectOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
            objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(serializable)
            objectOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayOutputStream?.close()
            objectOutputStream?.close()
        }
        return null
    }

    @TypeConverter
    fun toSerializable(byteArray: ByteArray?): Script? {
        byteArray ?: return null
        var byteArrayOutputStream: ByteArrayInputStream? = null
        var objectInputStream: ObjectInputStream? = null
        try {
            byteArrayOutputStream = ByteArrayInputStream(byteArray)
            objectInputStream = ObjectInputStream(byteArrayOutputStream)
            return objectInputStream.readObject() as Script
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayOutputStream?.close()
            objectInputStream?.close()
        }
        return null
    }
}

class DateConverter {
    @TypeConverter
    fun convertDate(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun revertData(value: Long): Date {
        return Date(value)
    }
}

class StringConverter {
    @TypeConverter
    fun convertContent(content: MutableState<String>): String {
        return content.value
    }

    @TypeConverter
    fun revertData(value: String): MutableState<String> {
        return mutableStateOf(value)
    }
}

class IntConverter {
    @TypeConverter
    fun convertContent(content: MutableState<Int>): Int {
        return content.value
    }

    @TypeConverter
    fun revertData(value: Int): MutableState<Int> {
        return mutableStateOf(value)
    }
}

class FloatConverter {
    @TypeConverter
    fun convertContent(content: MutableState<Float>): Float {
        return content.value
    }

    @TypeConverter
    fun revertData(value: Float): MutableState<Float> {
        return mutableStateOf(value)
    }
}

class BooleanConverter {
    @TypeConverter
    fun convertContent(content: MutableState<Boolean>): Boolean {
        return content.value
    }

    @TypeConverter
    fun revertData(value: Boolean): MutableState<Boolean> {
        return mutableStateOf(value)
    }
}

class ArrayStringConverter {
    @TypeConverter
    fun convertContent(content: MutableState<Array<String>>): String {
        val stringBuffer = StringBuffer("")
        content.value.forEach {
            stringBuffer.append(it)
            stringBuffer.append("￥|￥")
        }
        return stringBuffer.toString()
    }

    @TypeConverter
    fun revertData(value: String): MutableState<Array<String>> {
        val content = value.split("￥|￥")
        return mutableStateOf(content.toTypedArray())
    }
}

class ImageDataConverter {
    @TypeConverter
    fun convertContent(content: ImageData?): String {
        return if (content != null) {
            val savable = content.toSavable()
            Gson().toJson(savable)
        } else {
            "%NULL%"
        }
    }

    @TypeConverter
    fun revertData(value: String): ImageData? {
        return if (value == "%NULL%") {
            null
        } else {
            val savable = Gson().fromJson(value, SavableImage::class.java)
            savable.toImageData()
        }
    }
}

class BasicParamConverter {
    @TypeConverter
    fun convertContent(content: BasicParam): String {
        return when(content) {
            is TxtParam -> {
                val saveTxtParam = content.toSavable()
                Gson().toJson(saveTxtParam) + "&+SavableTxtParam+&"
            }

            is ImgParam -> {
                val savableImgParam = content.toSavable()
                Gson().toJson(savableImgParam) + "&+SavableImgParam+&"
            }

            else -> Gson().toJson(null)
        }
    }

    @TypeConverter
    fun revertData(value: String): BasicParam {
        if (value.endsWith("&+SavableTxtParam+&")) {
            val trueStr = value.replace("&+SavableTxtParam+&", "")
            val param =  Gson().fromJson(trueStr, SavableTxtParam::class.java)
            return param.toTxtParam()
        }
        if (value.endsWith("&+SavableImgParam+&")) {
            val trueStr = value.replace("&+SavableImgParam+&", "")
            val param =  Gson().fromJson(trueStr, SavableImgParam::class.java)
            return param.toImgParam()
        }
        return Gson().fromJson(value, BasicParam::class.java)
    }
}

class MJsonArrayConverter {
    @TypeConverter
    fun convertContent(content: MutableState<JsonArray>): String {
        if (content.value.size() == 0) {
            return ""
        }
        return content.value.asString
    }

    @TypeConverter
    fun revertData(value: String): MutableState<JsonArray> {
        if (value.isEmpty()) {
            return mutableStateOf(JsonArray())
        }
        return mutableStateOf(JsonParser().parse(value).asJsonArray)
    }
}

class SnapshotStateListConverter {
    @TypeConverter
    fun convertContent(content: SnapshotStateList<Int>): String {
        val sb = StringBuilder("")
        content.forEachIndexed { index, int ->
            if (index != content.lastIndex) {
                sb.append("$int")
                sb.append(", ")
            } else {
                sb.append("$int")
            }
        }
        return sb.toString()
    }

    @TypeConverter
    fun revertData(value: String): SnapshotStateList<Int> {
        val list = SnapshotStateList<Int>()
        val array = value.split(", ")
        array.forEach {
            if (it.isNotEmpty())
                list.add(it.toInt())
        }
        return list
    }
}