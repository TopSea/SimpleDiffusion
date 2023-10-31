import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object Base64Util {
    private val base64DecodeChars = byteArrayOf(
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        62,
        -1,
        -1,
        -1,
        63,
        52,
        53,
        54,
        55,
        56,
        57,
        58,
        59,
        60,
        61,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        47,
        48,
        49,
        50,
        51,
        -1,
        -1,
        -1,
        -1,
        -1
    )


    /**
     * 解密
     *
     * @param str
     * @return
     */
    fun decode(str: String): ByteArray {
        try {
            return decodePrivate(str)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return byteArrayOf()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun decodePrivate(str: String): ByteArray {
        val sb = StringBuffer()
        val data = str.toByteArray(charset("US-ASCII"))
        val len = data.size
        var i = 0
        var b1: Int
        var b2: Int
        var b3: Int
        var b4: Int
        while (i < len) {
            do {
                b1 = base64DecodeChars[data[i++].toInt()].toInt()
            } while (i < len && b1 == -1)
            if (b1 == -1) break
            do {
                b2 = base64DecodeChars[data[i++].toInt()].toInt()
            } while (i < len && b2 == -1)
            if (b2 == -1) break
            sb.append((b1 shl 2 or (b2 and 0x30 ushr 4)).toChar())
            do {
                b3 = data[i++].toInt()
                if (b3 == 61) return sb.toString().toByteArray(charset("iso8859-1"))
                b3 = base64DecodeChars[b3].toInt()
            } while (i < len && b3 == -1)
            if (b3 == -1) break
            sb.append((b2 and 0x0f shl 4 or (b3 and 0x3c ushr 2)).toChar())
            do {
                b4 = data[i++].toInt()
                if (b4 == 61) return sb.toString().toByteArray(charset("iso8859-1"))
                b4 = base64DecodeChars[b4].toInt()
            } while (i < len && b4 == -1)
            if (b4 == -1) break
            sb.append((b3 and 0x03 shl 6 or b4).toChar())
        }
        return sb.toString().toByteArray(charset("iso8859-1"))
    }

    /******************************************官方 */
    /**
     * 【官】解密
     * @param str - 纯字母，就不搞utf-8编码了
     * @return
     */
    fun decodeToBytes(str: String): ByteArray {
        return Base64.decode(str.toByteArray(Charset.forName("utf-8")), Base64.URL_SAFE)
    }
}