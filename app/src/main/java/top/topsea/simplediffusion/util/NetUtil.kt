package top.topsea.simplediffusion.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun String.isReceipt() = startsWith(Constant.TAG_RECEIPT_HEADER)

internal fun String.removeHeader() = if (isReceipt()) StringBuffer(this)
    .replace(0, getHeaderLength(), "")
    .toString() else this

internal fun getHeaderLength() = Constant.TAG_RECEIPT_HEADER.length

internal suspend fun <T> withMain(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main, block = block)
}

internal suspend fun <T> withIO(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO, block = block)
}
