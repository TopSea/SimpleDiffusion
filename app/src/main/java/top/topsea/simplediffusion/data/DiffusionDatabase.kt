package top.topsea.simplediffusion.data

import androidx.room.Database
import androidx.room.RoomDatabase
import top.topsea.simplediffusion.data.param.CNParam
import top.topsea.simplediffusion.data.param.CNParamDao
import top.topsea.simplediffusion.data.param.ImageData
import top.topsea.simplediffusion.data.param.ImageDataDao
import top.topsea.simplediffusion.data.param.ImgParam
import top.topsea.simplediffusion.data.param.ImgParamDao
import top.topsea.simplediffusion.data.param.TaskParam
import top.topsea.simplediffusion.data.param.TaskParamDao
import top.topsea.simplediffusion.data.param.TxtParam
import top.topsea.simplediffusion.data.param.TxtParamDao
import top.topsea.simplediffusion.data.param.UserPrompt
import top.topsea.simplediffusion.data.param.UserPromptDao

@Database(entities = [ImageData::class, UserPrompt::class, TaskParam::class,
    TxtParam::class, ImgParam::class, CNParam::class], version = 1,)
abstract class DiffusionDatabase: RoomDatabase() {
    abstract fun userPromptDao(): UserPromptDao
    abstract fun imageDataDao(): ImageDataDao
    abstract fun cnParamDao(): CNParamDao
    abstract fun txtParamDao(): TxtParamDao
    abstract fun imgParamDao(): ImgParamDao
    abstract fun taskParamDao(): TaskParamDao
}