package top.topsea.simplediffusion

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import top.topsea.simplediffusion.util.TextUtil

@HiltAndroidApp
class SimpleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
//        CrashHandler.instance!!.init(applicationContext)
        val rootDir = MMKV.initialize(this)
        TextUtil.topsea("onCreate: $rootDir")
    }
}