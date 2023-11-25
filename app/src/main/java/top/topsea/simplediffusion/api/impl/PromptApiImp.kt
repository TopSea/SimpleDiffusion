package top.topsea.simplediffusion.api.impl

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import top.topsea.simplediffusion.api.PromptApi
import top.topsea.simplediffusion.data.state.PromptCategory
import top.topsea.simplediffusion.data.state.PromptFile
import top.topsea.simplediffusion.util.Constant
import top.topsea.simplediffusion.util.TextUtil

class PromptApiImp(
    private val promptApi: PromptApi
) {
    suspend fun checkSdPrompt(): Boolean {
        return withContext(Dispatchers.IO) {
            val result = kotlin.runCatching {
                promptApi.getPromptVersion().execute()
            }
            var v = true
            val response = result.getOrNull()
            response?.let {
                if (it.isSuccessful) {
                    it.body()?.let { body ->
                        val str = body.string()
                        v = str.contains("Not Found")
                    }
                } else {
                }
            }
            v
        }
    }

    fun getAllPrompts(): Flow<List<PromptFile>> {
        return flow {
            val result = kotlin.runCatching {
                promptApi.getAllPrompts().execute()
            }
            val response = result.getOrNull()
            val models = ArrayList<PromptFile>()
            response?.let { it ->
                if (it.isSuccessful) {
                    TextUtil.topsea(it.body().toString(), Log.ERROR)
                    it.body()?.let { body ->
                        val jsonStr = body.string()

                        val parser = JsonParser()
                        val firstLayer = parser.parse(jsonStr).asJsonObject.entrySet()
                        val fi = firstLayer.iterator()
                        while (fi.hasNext()) {
                            // 第一层：各个文件
                            val (k1, v1) = fi.next() as Map.Entry<*, *>
                            val prompt = PromptFile()         // 以每个文件为基础区分出对象
                            prompt.filename = k1.toString()
                            val categories = ArrayList<PromptCategory>()

                            val si = (v1 as JsonObject).entrySet().iterator()
                            while (si.hasNext()) {
                                // 第二层：各个文件中的分类
                                val (k2, v2) = si.next() as Map.Entry<*, *>
                                val category = PromptCategory(k2.toString())
                                val pl = ArrayList<Pair<String, String>>()

                                val ti = (v2 as JsonObject).entrySet().iterator()
                                while (ti.hasNext()) {
                                    // 第三层：各个分类中的提示词
                                    val (k3, v3) = ti.next() as Map.Entry<*, *>
                                    if (v3 is JsonPrimitive) {
                                        val str = v3.toString()
                                        val trueStr = str.replace("\"", "")
                                        val addableStr = Constant.addableFirst + trueStr + "," + Constant.addableSecond
                                        pl.add(k3.toString() to addableStr)
                                    } else {
                                        val fourI = (v3 as JsonObject).entrySet().iterator()
                                        while (fourI.hasNext()) {
                                            // 第四层...
                                            val (k4, v4) = fourI.next() as Map.Entry<*, *>
                                            pl.add(k4.toString() to v4.toString())
                                        }
                                    }
                                }
                                category.prompts = pl
                                categories.add(category)
                            }
                            prompt.categories = categories
                            models.add(prompt)
                        }

                    }
                } else {
                }
            }
            emit(models)
        }.flowOn(Dispatchers.IO)
    }
}