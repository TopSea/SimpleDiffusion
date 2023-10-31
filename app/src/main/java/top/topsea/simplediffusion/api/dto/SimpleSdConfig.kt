package top.topsea.simplediffusion.api.dto

import androidx.annotation.Keep
import top.topsea.simplediffusion.util.TextUtil

@Keep
data class SimpleSdConfig <T> (
    var configName: String,
    var value: T,
) {
    override fun toString(): String {
        TextUtil.topsea("toString: simpleName = ${value!!::class.java.simpleName}")

        when (value!!::class.java.simpleName) {
            "String[]" -> {
                return "{\n" +
                        "  \"$configName\": ${asToASJson(value as Array<String>)}\n" +
                        "}"
            }
            "String" -> {
                return "{\n" +
                        "  \"$configName\": \"${value}\"\n" +
                        "}"
            }
        }

        return "{\n" +
                "  \"$configName\": ${value}\n" +
                "}"
    }
}

fun asToASJson(aString: Array<String>): String {
    val strB = StringBuffer()
    val strs = aString.contentToString().split(", ")
    strs.forEachIndexed { index, s ->
        if (index == 0) {
            strB.append(s.replace("[", "[\""))
            strB.append("\", ")
        } else if (index == strs.lastIndex) {
            strB.append("\"")
            strB.append(s.replace("]", "\"]"))
        } else {
            strB.append("\"$s\", ")
        }
    }

    return strB.toString()
}
