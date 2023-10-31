package top.topsea.simplediffusion.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.Constant.SERVER_PORT
import top.topsea.simplediffusion.util.TextUtil
import top.topsea.simplediffusion.util.isReceipt
import top.topsea.simplediffusion.util.removeHeader
import top.topsea.simplediffusion.util.withMain
import java.net.Socket
import java.net.SocketException

class SocketClient(
    var address: String,
    var onLicked: ((String?, Exception?) -> Unit)? = null
) {

    private val scope = MainScope()

    private var socket: Socket? = null

    @Synchronized
    private fun getSocket(block: (Socket) -> Unit) {
        socket?.apply {
            block.invoke(this)
        } ?: let {
            scope.launch(Dispatchers.IO) {
                try {
                    socket = Socket(address, SERVER_PORT).apply {
                        TextUtil.topsea("启动客户端,IP:${this.inetAddress}端口号:${this.port}")
                        onLicked?.invoke(inetAddress?.hostAddress, null)
                        block.invoke(this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onLicked?.invoke("", e)
                }
            }
        }
    }

    fun startClient(
        onReceive: ((String?) -> Unit)? = null,
        success: ((Exception?) -> Unit)? = null
    ) {
        getSocket {
            scope.receive(it, onReceive, success)
        }
    }

    fun sendMsg(msg: String, result: ((Exception?) -> Unit)? = null) {
        getSocket {
            scope.send(socket, msg, result)
        }
    }

    fun close(onCutoff: ((String?) -> Unit)? = null) {
        socket?.closed(onCutoff)
        scope.cancel()
        socket = null
    }
}

internal fun Socket.closed(onCutoff: ((String?) -> Unit)? = null) {
    try {
        close()
        TextUtil.topsea("关闭客户端")
        onCutoff?.invoke(this.inetAddress?.hostAddress)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal fun CoroutineScope.send(
    socket: Socket?,
    msg: String,
    result: ((Exception?) -> Unit)? = null,
    onCutoff: ((String?) -> Unit)? = null
): Job {
    return this.launch(Dispatchers.IO) {
        try {
            withTimeout(Constant.TIME_OUT_SEND) {
                socket?.getOutputStream()?.let {
                    TextUtil.topsea("客户端发送:$msg")
                    it.write(msg.toByteArray(Charsets.UTF_8))
                    it.flush()
                    withMain { result?.invoke(null) }
                }
                    ?: withMain { result?.invoke(NullPointerException("socket or OutputStream is null")) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            socket?.closed(onCutoff)
            withMain { result?.invoke(e) }
        }
    }
}

internal fun CoroutineScope.receive(
    socket: Socket?,
    onReceive: ((String?) -> Unit)? = null,
    success: ((Exception?) -> Unit)? = null,
    onCutoff: ((String?) -> Unit)? = null
): Job {
    return this.launch(Dispatchers.IO) {
        socket?.apply {
            if (isClosed) {
                withMain { success?.invoke(SocketException("Socket is closed")) }
            }
            while (!isClosed) {
                try {
                    getInputStream()?.use { input ->
                        withMain { success?.invoke(null) }
                        val buffer = ByteArray(1024)
                        var len: Int
                        while (input.read(buffer).also { len = it } != -1) {
                            val msg = String(buffer, 0, len)
                            if (!msg.isReceipt()) {
                                getOutputStream()?.let {
                                    it.write(
                                        "${Constant.TAG_RECEIPT_HEADER}客户端回执:已收到消息".toByteArray(
                                            Charsets.UTF_8
                                        )
                                    )
                                    it.flush()
                                }
                            }
                            val s = msg.removeHeader()
                            withMain { onReceive?.invoke(s) }
                            TextUtil.topsea("收到来自${socket.inetAddress.hostAddress}:${socket.port}的数据为：$s")
                        }
                        withMain { onReceive?.invoke("断开连接") }
                        socket.closed(onCutoff)
                    }
                        ?: withMain { success?.invoke(NullPointerException("socket or InputStream is null")) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    socket.closed(onCutoff)
                    withMain { success?.invoke(e) }
                }
            }
        } ?: withMain { success?.invoke(NullPointerException("socket is null")) }
    }
}
